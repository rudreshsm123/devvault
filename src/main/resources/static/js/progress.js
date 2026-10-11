/* My Progress: which problems and lessons a visitor finished, and how each problem attempt went.
   Signed out: saved in this browser only (localStorage), nothing is sent anywhere.
   Signed in: saved on the server so it follows the user to any device. Progress collected while signed out
   is merged into the account the first time the user signs in.

   Each saved item looks like {t: title, u: url, at: millis, r: "right" | "wrong"}. A missing r means "right".
   "wrong" means the learner attempted the problem but did not solve it, so it is listed under "Practise again". */
(function () {
    'use strict';

    var KEY = 'devvault.progress.v1';

    function meta(name) {
        var node = document.querySelector('meta[name="' + name + '"]');
        return node ? node.getAttribute('content') : null;
    }

    var user = meta('devvault-user');            // set by the server only when someone is signed in
    var csrfToken = meta('_csrf');
    var csrfHeader = meta('_csrf_header');

    /* ---------- browser storage (signed out) ---------- */
    function loadLocal() {
        try {
            var raw = window.localStorage.getItem(KEY);
            var data = raw ? JSON.parse(raw) : {};
            data.problem = data.problem || {};
            data.lesson = data.lesson || {};
            return data;
        } catch (e) {
            return { problem: {}, lesson: {} };
        }
    }

    function saveLocal(data) {
        try {
            window.localStorage.setItem(KEY, JSON.stringify(data));
        } catch (e) {
            // Private window or blocked storage: progress simply will not persist.
        }
    }

    function isCount(data) {
        return Object.keys(data.problem).length + Object.keys(data.lesson).length;
    }

    function safeGet(k) { try { return window.localStorage.getItem(k); } catch (e) { return null; } }
    function safeSet(k, v) { try { window.localStorage.setItem(k, v); } catch (e) { /* ignore */ } }

    /* ---------- server storage (signed in) ---------- */
    function api(method, path, body, keepalive) {
        var headers = { 'Content-Type': 'application/json' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        return window.fetch(path, {
            method: method,
            headers: headers,
            credentials: 'same-origin',
            keepalive: !!keepalive,                // lets a save finish even when the page navigates away
            body: body ? JSON.stringify(body) : undefined
        }).then(function (response) {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.json();
        });
    }

    var cache = null;                              // the user's progress, loaded once per page

    function loadAll() {
        if (cache) {
            return cache;
        }
        if (!user) {
            cache = Promise.resolve(loadLocal());
            return cache;
        }
        var mergedFlag = KEY + '.merged.' + user;
        var local = loadLocal();
        var needsMerge = isCount(local) > 0 && !safeGet(mergedFlag);
        var start = needsMerge ? api('POST', '/api/me/progress/import', local).then(function (merged) {
            safeSet(mergedFlag, '1');
            return merged;
        }) : api('GET', '/api/me/progress');
        cache = start.then(function (data) {
            data.problem = data.problem || {};
            data.lesson = data.lesson || {};
            return data;
        }).catch(function () {
            return loadLocal();                    // server unreachable: fall back to what the browser knows
        });
        return cache;
    }

    function has(data, kind, id) {
        return Object.prototype.hasOwnProperty.call(data[kind], id);
    }

    /** Finished = saved and not marked as a wrong attempt. */
    function isDone(data, kind, id) {
        return has(data, kind, id) && data[kind][id].r !== 'wrong';
    }

    function isWrong(data, kind, id) {
        return has(data, kind, id) && data[kind][id].r === 'wrong';
    }

    /** Flips "done" for one item and resolves with true when it is now done. */
    function toggle(kind, id, title, url) {
        if (user) {
            return api('POST', '/api/me/progress/toggle', { kind: kind, id: Number(id), title: title, url: url })
                .then(function (result) {
                    return loadAll().then(function (data) {
                        if (result.done) {
                            data[kind][id] = { t: title, u: url, at: Date.now(), r: 'right' };
                        } else {
                            delete data[kind][id];
                        }
                        return result.done;
                    });
                });
        }
        var data = loadLocal();
        if (isWrong(data, kind, id)) {
            data[kind][id] = { t: title, u: url, at: Date.now(), r: 'right' };
        } else if (has(data, kind, id)) {
            delete data[kind][id];
        } else {
            data[kind][id] = { t: title, u: url, at: Date.now(), r: 'right' };
        }
        saveLocal(data);
        cache = Promise.resolve(data);
        return Promise.resolve(isDone(data, kind, id));
    }

    /** Saves how a problem attempt went ("right" or "wrong") and resolves with that result. */
    function saveResult(kind, id, title, url, result, keepalive) {
        if (user) {
            return api('POST', '/api/me/progress/result',
                { kind: kind, id: Number(id), title: title, url: url, result: result }, keepalive)
                .then(function () {
                    return loadAll().then(function (data) {
                        data[kind][id] = { t: title, u: url, at: Date.now(), r: result };
                        return result;
                    });
                });
        }
        var data = loadLocal();
        data[kind][id] = { t: title, u: url, at: Date.now(), r: result };
        saveLocal(data);
        cache = Promise.resolve(data);
        return Promise.resolve(result);
    }

    /* ---------- "Mark as done" buttons on problem and lesson pages ---------- */
    function initToggles() {
        document.querySelectorAll('.progress-toggle').forEach(function (box) {
            var kind = box.dataset.kind, id = box.dataset.id;
            var button = document.createElement('button');
            button.type = 'button';
            button.className = 'progress-btn';

            function paint(done) {
                button.textContent = done ? '✓ Done (click to undo)' : 'Mark as done';
                button.classList.toggle('is-done', done);
                button.setAttribute('aria-pressed', done ? 'true' : 'false');
            }
            paint(false);
            loadAll().then(function (data) { paint(isDone(data, kind, id)); });

            button.addEventListener('click', function () {
                button.disabled = true;
                toggle(kind, id, box.dataset.title, box.dataset.url)
                    .then(paint)
                    .catch(function () { button.textContent = 'Could not save, try again'; })
                    .then(function () { button.disabled = false; });
            });
            box.appendChild(button);

            var link = document.createElement('a');
            link.href = '/progress';
            link.className = 'progress-link';
            link.textContent = user ? 'See my progress (saved to your account)' : 'See my progress';
            box.appendChild(link);
        });
    }

    /* ---------- "How did you do?" after revealing a solution ---------- */
    function initSelfCheck() {
        document.querySelectorAll('.selfcheck').forEach(function (box) {
            var kind = box.dataset.kind || 'problem', id = box.dataset.id;
            var message = box.querySelector('.sc-msg');
            var buttons = box.querySelectorAll('[data-result]');

            function paint(result) {
                buttons.forEach(function (b) { b.classList.toggle('chosen', b.dataset.result === result); });
                if (!message) {
                    return;
                }
                message.textContent = result === 'right'
                    ? 'Saved: marked as solved ✓'
                    : result === 'wrong'
                        ? 'Saved: marked for more practice. It appears under "Practise again" in My Progress.'
                        : '';
            }

            loadAll().then(function (data) {
                paint(has(data, kind, id) ? (data[kind][id].r || 'right') : null);
            });

            buttons.forEach(function (button) {
                button.addEventListener('click', function () {
                    // In the owner's copy the button also submits a form that updates the practice status;
                    // the save below must therefore survive the page change (keepalive).
                    var result = button.dataset.result;
                    saveResult(kind, id, box.dataset.title, box.dataset.url, result, true)
                        .then(paint)
                        .catch(function () { if (message) { message.textContent = 'Could not save, try again.'; } });
                });
            });
        });
    }

    /* ---------- check / cross on list rows ---------- */
    function markLists() {
        var links = document.querySelectorAll('main a[href]');
        if (links.length === 0) {
            return;
        }
        loadAll().then(function (data) {
            links.forEach(function (a) {
                var m = /^\/(problems|learn)\/(\d+)$/.exec(a.getAttribute('href'));
                if (!m) {
                    return;
                }
                var kind = m[1] === 'problems' ? 'problem' : 'lesson';
                var holder = a.closest('tr') || a.closest('li');
                if (!holder) {
                    return;
                }
                if (isDone(data, kind, m[2])) {
                    holder.classList.add('done');
                } else if (isWrong(data, kind, m[2])) {
                    holder.classList.add('wrong');
                }
            });
        });
    }

    /* ---------- the /progress page ---------- */
    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) {
            node.className = className;
        }
        if (text !== undefined) {
            node.textContent = text;
        }
        return node;
    }

    function renderBar(label, done, total) {
        var pct = total > 0 ? Math.round(done * 100 / total) : 0;
        var wrap = el('div', 'progress-bar-wrap');
        wrap.appendChild(el('div', 'progress-bar-label', label + ': ' + done + ' of ' + total + ' (' + pct + '%)'));
        var track = el('div', 'progress-track');
        var fill = el('div', 'progress-fill');
        fill.style.width = pct + '%';
        track.appendChild(fill);
        wrap.appendChild(track);
        return wrap;
    }

    function renderList(title, items, emptyText, className) {
        var section = el('section', 'guide-section');
        section.appendChild(el('h3', null, title + ' (' + items.length + ')'));
        if (items.length === 0) {
            section.appendChild(el('p', 'muted', emptyText));
            return section;
        }
        var ul = el('ul', 'result-list' + (className ? ' ' + className : ''));
        items.sort(function (a, b) { return b.at - a.at; }).forEach(function (item) {
            var li = el('li');
            var a = el('a', null, item.t);
            if (typeof item.u === 'string' && item.u.charAt(0) === '/' && item.u.charAt(1) !== '/') {
                a.href = item.u;                    // same-site paths only
            }
            li.appendChild(a);
            li.appendChild(el('span', 'muted', new Date(item.at).toLocaleDateString()));
            ul.appendChild(li);
        });
        section.appendChild(ul);
        return section;
    }

    function initProgressPage() {
        var root = document.getElementById('progress-root');
        if (!root) {
            return;
        }
        root.appendChild(el('p', 'muted', user
            ? 'Signed in as ' + user + ': your progress is saved on the server and follows you to any device.'
            : 'You are not signed in: progress is saved in this browser only. Sign in to keep it on every device.'));

        loadAll().then(function (data) {
            var all = function (kind) { return Object.keys(data[kind]).map(function (k) { return data[kind][k]; }); };
            var lessons = all('lesson');
            var solved = all('problem').filter(function (p) { return p.r !== 'wrong'; });
            var wrong = all('problem').filter(function (p) { return p.r === 'wrong'; });
            var totalProblems = parseInt(root.dataset.problems, 10) || 0;
            var totalLessons = parseInt(root.dataset.lessons, 10) || 0;
            var attempted = solved.length + wrong.length;

            var summary = el('section', 'guide-section');
            summary.appendChild(renderBar('Lessons finished', lessons.length, totalLessons));
            summary.appendChild(renderBar('Problems solved', solved.length, totalProblems));
            if (attempted > 0) {
                summary.appendChild(el('p', null, 'Self-checked accuracy: ' + Math.round(solved.length * 100 / attempted)
                    + '% (' + solved.length + ' right, ' + wrong.length + ' wrong)'));
            }
            summary.appendChild(el('p', 'muted', 'Certificates are earned from this progress.'));
            var certLink = el('a', null, 'View my certificates');
            certLink.href = '/certificates';
            summary.appendChild(certLink);
            root.appendChild(summary);

            root.appendChild(renderList('Practise again (marked wrong)', wrong,
                'Nothing to practise again. Problems you mark as "I got it wrong" show up here.', 'wrong-list'));
            root.appendChild(renderList('Problems solved', solved,
                'Nothing here yet. Finish a problem and press "I got it right" or "Mark as done".'));
            root.appendChild(renderList('Lessons finished', lessons,
                'Nothing here yet. Finish a lesson and press "Mark as done".'));

            if (!user) {
                var reset = el('button', 'btn-danger', 'Reset my progress');
                reset.type = 'button';
                reset.addEventListener('click', function () {
                    if (window.confirm('Clear all progress saved in this browser? This cannot be undone.')) {
                        saveLocal({ problem: {}, lesson: {} });
                        window.location.reload();
                    }
                });
                root.appendChild(reset);
            }
        });
    }

    /* Other scripts on the certificate pages read the same progress through this small public handle. */
    window.DevVaultProgress = {
        load: loadAll,
        isDone: isDone,
        user: function () { return user; }
    };

    function init() {
        initToggles();
        initSelfCheck();
        markLists();
        initProgressPage();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
