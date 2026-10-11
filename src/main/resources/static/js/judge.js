/* Code Challenge: write code, press "Run tests", get an instant verdict.
   JavaScript runs inside a Web Worker in the visitor's own browser (nothing is sent to the server).
   Java is sent to the server's Java judge, which is only switched on for a copy running on your own computer. */
(function () {
    'use strict';

    var challenge = window.DEVVAULT_CHALLENGE;
    var panel = document.getElementById('challenge');
    if (!challenge || !panel) {
        return;
    }

    var langSelect = document.getElementById('ch-lang');
    var editor = document.getElementById('ch-code');
    var runButton = document.getElementById('ch-run');
    var resetButton = document.getElementById('ch-reset');
    var javaNote = document.getElementById('ch-java-note');
    var resultBox = document.getElementById('ch-result');
    var pageMeta = document.querySelector('meta[name="devvault-user"]');
    var drafts = { js: challenge.jsStarter, java: challenge.javaStarter };
    var current = 'js';

    function meta(name) {
        var node = document.querySelector('meta[name="' + name + '"]');
        return node ? node.getAttribute('content') : null;
    }

    /* ---------- the code that runs inside the Web Worker (kept as a function so it is easy to read) ---------- */
    function workerMain() {
        function norm(v) {
            if (v === undefined || v === null) { return null; }
            if (typeof v === 'number') { return Object.is(v, -0) ? 0 : (isFinite(v) ? v : null); }
            if (Array.isArray(v)) { return v.map(norm); }
            if (typeof v === 'object') {
                var o = {};
                Object.keys(v).sort().forEach(function (k) { o[k] = norm(v[k]); });
                return o;
            }
            return v;
        }
        self.onmessage = function (e) {
            var d = e.data, fn;
            try {
                fn = (new Function(d.code + '\n;return (typeof ' + d.fn + ' === "function") ? ' + d.fn + ' : undefined;'))();
            } catch (err) {
                self.postMessage({ status: 'COMPILE_ERROR', message: String(err) });
                return;
            }
            if (typeof fn !== 'function') {
                self.postMessage({ status: 'COMPILE_ERROR',
                    message: 'The function "' + d.fn + '" was not found. Keep the function name from the starter code.' });
                return;
            }
            var results = [], passed = 0, firstFailure = null;
            for (var i = 0; i < d.tests.length; i++) {
                var t = d.tests[i], ok = false, error = '', actualText = '';
                try {
                    var actual = fn.apply(null, JSON.parse(JSON.stringify(t.args)));
                    actualText = JSON.stringify(norm(actual));
                    ok = actualText === JSON.stringify(norm(t.expected));
                } catch (err) {
                    error = String(err);
                }
                if (ok) { passed++; } else if (firstFailure === null) { firstFailure = error ? 'RUNTIME_ERROR' : 'WRONG_ANSWER'; }
                results.push({ ok: ok, input: JSON.stringify(t.args), expected: JSON.stringify(t.expected),
                    actual: actualText, error: error });
            }
            self.postMessage({ status: firstFailure || 'ACCEPTED', passed: passed, total: results.length, results: results });
        };
    }

    function runJavaScript(code) {
        return new Promise(function (resolve) {
            var url = URL.createObjectURL(new Blob(['(' + workerMain.toString() + ')()'], { type: 'text/javascript' }));
            var worker = new Worker(url);
            var timer = window.setTimeout(function () {          // an endless loop only blocks the worker, which we stop
                worker.terminate();
                URL.revokeObjectURL(url);
                resolve({ status: 'TIME_LIMIT', message: 'Time limit exceeded: your code ran for more than 4 seconds. '
                    + 'Is there an endless loop?', results: [], passed: 0, total: challenge.tests.length });
            }, 4000);
            worker.onmessage = function (e) {
                window.clearTimeout(timer);
                worker.terminate();
                URL.revokeObjectURL(url);
                resolve(e.data);
            };
            worker.onerror = function (e) {
                window.clearTimeout(timer);
                worker.terminate();
                URL.revokeObjectURL(url);
                resolve({ status: 'COMPILE_ERROR', message: e.message || 'Your code could not be run.' });
            };
            worker.postMessage({ code: code, fn: challenge.functionName, tests: challenge.tests });
        });
    }

    function runJava(code) {
        var headers = { 'Content-Type': 'application/json' };
        var token = meta('_csrf'), header = meta('_csrf_header');
        if (token && header) {
            headers[header] = token;
        }
        return window.fetch('/api/judge/java', {
            method: 'POST', headers: headers, credentials: 'same-origin',
            body: JSON.stringify({ problemId: challenge.problemId, code: code })
        }).then(function (r) {
            if (!r.ok) { throw new Error('HTTP ' + r.status); }
            return r.json();
        });
    }

    /* ---------- showing the verdict ---------- */
    var LABELS = {
        ACCEPTED: 'Accepted', WRONG_ANSWER: 'Wrong Answer', RUNTIME_ERROR: 'Runtime Error',
        TIME_LIMIT: 'Time Limit Exceeded', COMPILE_ERROR: 'Compile Error', DISABLED: 'Java judge is off',
        BUSY: 'Judge busy', INVALID: 'Cannot run'
    };

    function el(tag, className, text) {
        var node = document.createElement(tag);
        if (className) { node.className = className; }
        if (text !== undefined) { node.textContent = text; }
        return node;
    }

    function show(verdict) {
        resultBox.textContent = '';
        var accepted = verdict.status === 'ACCEPTED';
        var banner = el('div', 'ch-banner ' + (accepted ? 'ch-ok' : 'ch-bad'),
            (LABELS[verdict.status] || verdict.status)
            + (verdict.total ? ' - ' + verdict.passed + ' / ' + verdict.total + ' tests passed' : ''));
        resultBox.appendChild(banner);
        if (verdict.message) {
            resultBox.appendChild(el('pre', 'ch-message', verdict.message));
        }
        if (verdict.results && verdict.results.length > 0) {
            var table = el('table', 'ch-table');
            var head = el('tr');
            ['#', 'Input', 'Expected', 'Your output', ''].forEach(function (h) { head.appendChild(el('th', null, h)); });
            table.appendChild(head);
            verdict.results.forEach(function (r, i) {
                var row = el('tr', r.ok ? 'ch-pass' : 'ch-fail');
                row.appendChild(el('td', null, String(i + 1)));
                row.appendChild(el('td', 'ch-mono', r.input));
                row.appendChild(el('td', 'ch-mono', r.expected));
                row.appendChild(el('td', 'ch-mono', r.error ? r.error : (r.timeout ? 'timed out' : r.actual)));
                row.appendChild(el('td', null, r.ok ? '✓' : '✗'));
                table.appendChild(row);
            });
            resultBox.appendChild(table);
        }
        if (accepted) {
            resultBox.appendChild(el('p', 'ch-next', 'All tests passed. This problem is now marked as solved. '
                + 'Submit your attempt below to unlock the solution and the line-by-line explanation.'));
            markSolved();
        } else if (verdict.results && verdict.results.length > 0) {
            resultBox.appendChild(el('p', 'muted', 'Fix the failing tests and run again.'));
        }
    }

    function markSolved() {
        var url = '/problems/' + challenge.problemId;
        var title = document.querySelector('.content-header h2');
        if (window.DevVaultProgress && window.DevVaultProgress.saveResult) {
            window.DevVaultProgress.saveResult('problem', String(challenge.problemId),
                title ? title.textContent : 'Problem ' + challenge.problemId, url, 'right', false)
                .catch(function () { /* progress is a bonus; ignore failures */ });
        }
        if (challenge.canSetStatus) {                      // your own copy also updates the practice status
            var headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
            var token = meta('_csrf'), header = meta('_csrf_header');
            if (token && header) { headers[header] = token; }
            window.fetch(url + '/status', { method: 'POST', headers: headers, credentials: 'same-origin',
                redirect: 'manual', body: 'status=LEARNED' }).catch(function () { /* ignore */ });
        }
    }

    function copyToAttempt(code) {
        var attempt = document.querySelector('textarea[name="userAttempt"]');
        if (attempt && (attempt.value === '' || attempt.dataset.synced === 'true')) {
            attempt.value = code;
            attempt.dataset.synced = 'true';
        }
    }

    /* ---------- wiring ---------- */
    function setLanguage(lang) {
        drafts[current] = editor.value;
        current = lang;
        editor.value = drafts[lang];
        var javaOff = lang === 'java' && !challenge.javaEnabled;
        javaNote.hidden = !javaOff;
        runButton.disabled = javaOff;
    }

    langSelect.addEventListener('change', function () { setLanguage(langSelect.value); });

    resetButton.addEventListener('click', function () {
        editor.value = current === 'js' ? challenge.jsStarter : challenge.javaStarter;
        drafts[current] = editor.value;
    });

    editor.addEventListener('keydown', function (e) {       // Tab inserts spaces instead of leaving the box
        if (e.key === 'Tab' && !e.shiftKey) {
            e.preventDefault();
            var start = editor.selectionStart, end = editor.selectionEnd;
            editor.value = editor.value.slice(0, start) + '  ' + editor.value.slice(end);
            editor.selectionStart = editor.selectionEnd = start + 2;
        }
    });

    runButton.addEventListener('click', function () {
        var code = editor.value;
        copyToAttempt(code);
        runButton.disabled = true;
        runButton.textContent = 'Running...';
        resultBox.textContent = '';
        var job = current === 'js' ? runJavaScript(code) : runJava(code);
        job.then(show).catch(function (err) {
            show({ status: 'INVALID', message: 'Could not run your code: ' + err.message });
        }).then(function () {
            runButton.disabled = current === 'java' && !challenge.javaEnabled;
            runButton.textContent = 'Run tests';
        });
    });

    if (!challenge.javaEnabled) {
        langSelect.querySelector('option[value="java"]').textContent = 'Java (your own computer only)';
    }
    editor.value = drafts.js;
    void pageMeta;
})();
