/* My Progress: remembers which problems and lessons a visitor finished, in this browser only.
   Nothing is sent to the server, so it also works on the read-only public site. */
(function () {
    'use strict';

    var KEY = 'devvault.progress.v1';

    function load() {
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

    function save(data) {
        try {
            window.localStorage.setItem(KEY, JSON.stringify(data));
            return true;
        } catch (e) {
            return false;                       // private window or blocked storage
        }
    }

    function isDone(data, kind, id) {
        return Object.prototype.hasOwnProperty.call(data[kind], id);
    }

    function toggle(kind, id, title, url) {
        var data = load();
        if (isDone(data, kind, id)) {
            delete data[kind][id];
        } else {
            data[kind][id] = { t: title, u: url, at: Date.now() };
        }
        save(data);
        return !isDone(data, kind, id) ? false : true;
    }

    /* ---- "Mark as done" buttons on problem and lesson pages ---- */
    function initToggles() {
        document.querySelectorAll('.progress-toggle').forEach(function (box) {
            var kind = box.dataset.kind, id = box.dataset.id;
            var button = document.createElement('button');
            button.type = 'button';
            button.className = 'progress-btn';

            function render() {
                var done = isDone(load(), kind, id);
                button.textContent = done ? '✓ Done (click to undo)' : 'Mark as done';
                button.classList.toggle('is-done', done);
                button.setAttribute('aria-pressed', done ? 'true' : 'false');
            }
            button.addEventListener('click', function () {
                toggle(kind, id, box.dataset.title, box.dataset.url);
                render();
            });
            render();
            box.appendChild(button);

            var link = document.createElement('a');
            link.href = '/progress';
            link.className = 'progress-link';
            link.textContent = 'See my progress';
            box.appendChild(link);
        });
    }

    /* ---- green check on list rows the visitor already finished ---- */
    function markLists() {
        var data = load();
        document.querySelectorAll('main a[href]').forEach(function (a) {
            var m = /^\/(problems|learn)\/(\d+)$/.exec(a.getAttribute('href'));
            if (!m) {
                return;
            }
            var kind = m[1] === 'problems' ? 'problem' : 'lesson';
            if (!isDone(data, kind, m[2])) {
                return;
            }
            var holder = a.closest('tr') || a.closest('li');
            if (holder && !holder.classList.contains('done')) {
                holder.classList.add('done');
            }
        });
    }

    /* ---- the /progress page ---- */
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
            a.href = item.u;
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
        var data = load();
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

        var reset = el('button', 'btn-danger', 'Reset my progress');
        reset.type = 'button';
        reset.addEventListener('click', function () {
            if (window.confirm('Clear all progress saved in this browser? This cannot be undone.')) {
                save({ problem: {}, lesson: {} });
                window.location.reload();
            }
        });
        root.appendChild(reset);
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
