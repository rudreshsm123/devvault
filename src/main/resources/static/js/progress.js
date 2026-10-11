/* My Progress: which problems and lessons a visitor finished.
   Signed out: saved in this browser only (localStorage), nothing is sent anywhere.
   Signed in: saved on the server so it follows the user to any device. Progress collected while signed out
   is merged into the account the first time the user signs in. */
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

    /* ---------- server storage (signed in) ---------- */
    function api(method, path, body) {
        var headers = { 'Content-Type': 'application/json' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        return window.fetch(path, {
            method: method,
            headers: headers,
            credentials: 'same-origin',
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
        cache = start.catch(function () {
            return loadLocal();                    // server unreachable: fall back to what the browser knows
        });
        return cache;
    }

    function safeGet(k) { try { return window.localStorage.getItem(k); } catch (e) { return null; } }
    function safeSet(k, v) { try { window.localStorage.setItem(k, v); } catch (e) { /* ignore */ } }

    function has(data, kind, id) {
        return Object.prototype.hasOwnProperty.call(data[kind], id);
    }

    /** Flips one item and resolves with true when it is now done. */
    function toggle(kind, id, title, url) {
        if (user) {
            return api('POST', '/api/me/progress/toggle', { kind: kind, id: Number(id), title: title, url: url })
                .then(function (result) {
                    return loadAll().then(function (data) {
                        if (result.done) {
                            data[kind][id] = { t: title, u: url, at: Date.now() };
                        } else {
                            delete data[kind][id];
                        }
                        return result.done;
                    });
                });
        }
        var data = loadLocal();
        if (has(data, kind, id)) {
            delete data[kind][id];
        } else {
            data[kind][id] = { t: title, u: url, at: Date.now() };
        }
        saveLocal(data);
        cache = Promise.resolve(data);
        return Promise.resolve(has(data, kind, id));
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
            loadAll().then(function (data) { paint(has(data, kind, id)); });

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

    /* ---------- green check on list rows the visitor already finished ---------- */
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
                if (!has(data, kind, m[2])) {
                    return;
                }
                var holder = a.closest('tr') || a.closest('li');
                if (holder) {
                    holder.classList.add('done');
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

    function renderList(title, items) {
        var section = el('section', 'guide-section');
        section.appendChild(el('h3', null, title + ' (' + items.length + ')'));
        if (items.length === 0) {
            section.appendChild(el('p', 'muted', 'Nothing here yet. Finish one and press "Mark as done".'));
            return section;
        }
        var ul = el('ul', 'result-list');
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
        var banner = el('p', 'muted', user
            ? 'Signed in as ' + user + ': your progress is saved on the server and follows you to any device.'
            : 'You are not signed in: progress is saved in this browser only. Sign in to keep it on every device.');
        root.appendChild(banner);

        loadAll().then(function (data) {
            var problems = Object.keys(data.problem).map(function (k) { return data.problem[k]; });
            var lessons = Object.keys(data.lesson).map(function (k) { return data.lesson[k]; });
            var totalProblems = parseInt(root.dataset.problems, 10) || 0;
            var totalLessons = parseInt(root.dataset.lessons, 10) || 0;

            var summary = el('section', 'guide-section');
            summary.appendChild(renderBar('Lessons', lessons.length, totalLessons));
            summary.appendChild(renderBar('Problems', problems.length, totalProblems));
            root.appendChild(summary);
            root.appendChild(renderList('Finished lessons', lessons));
            root.appendChild(renderList('Finished problems', problems));

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

    function init() {
        initToggles();
        markLists();
        initProgressPage();
    }

    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', init);
    } else {
        init();
    }
})();
