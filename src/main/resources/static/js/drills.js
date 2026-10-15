/* Practice drill page: sends the answer to the server, shows right or wrong, and saves the result
   to My Progress (browser storage, or the account when signed in). */
(function () {
    'use strict';

    var box = document.getElementById('drill');
    if (!box) {
        return;
    }
    var button = document.getElementById('check-btn');
    var verdict = document.getElementById('verdict');
    var csrfToken = (document.querySelector('meta[name="_csrf"]') || {}).content;
    var csrfHeader = (document.querySelector('meta[name="_csrf_header"]') || {}).content;

    function currentAnswer() {
        var radio = box.querySelector('input[type="radio"]:checked');
        if (radio) {
            return radio.value;
        }
        var input = document.getElementById('answer-input');
        return input ? input.value : '';
    }

    function show(correct, data) {
        verdict.hidden = false;
        verdict.className = 'drill-verdict ' + (correct ? 'is-right' : 'is-wrong');
        verdict.querySelector('.verdict-line').textContent = correct ? '✓ Right!' : '✗ Wrong';
        verdict.querySelector('.verdict-correct').textContent = correct ? '' : 'Correct answer: ' + data.answer;
        verdict.querySelector('.verdict-why').textContent = data.explanation;
    }

    /* ---------- "Explain my answer with AI" (signed-in users only; the server falls back to the built-in text) ---------- */
    var signedIn = !!document.querySelector('meta[name="devvault-user"]');
    var aiBox = document.getElementById('ai-box');
    var aiButton = document.getElementById('ai-btn');
    var aiNote = document.getElementById('ai-note');
    var aiText = document.getElementById('ai-text');
    var lastAnswer = '';

    var aiEnabled = box.dataset.ai === 'true';           // false on a server without an AI provider
    var aiSignin = document.getElementById('ai-signin');

    function offerAi(answer) {
        if (!aiBox || !aiEnabled) { return; }               // nothing to offer when AI is switched off
        lastAnswer = answer;
        aiBox.hidden = false;
        aiText.textContent = '';
        aiNote.textContent = '';
        aiButton.hidden = !signedIn;
        aiSignin.hidden = signedIn;
    }

    if (aiButton) {
        aiButton.addEventListener('click', function () {
            aiButton.disabled = true;
            aiNote.textContent = 'Thinking...';
            var headers = { 'Content-Type': 'application/json' };
            if (csrfToken && csrfHeader) { headers[csrfHeader] = csrfToken; }
            fetch('/api/me/ai/explain', {
                method: 'POST', headers: headers, credentials: 'same-origin',
                body: JSON.stringify({ drillId: Number(box.dataset.id), answer: lastAnswer })
            }).then(function (r) {
                return r.json().then(function (data) { return { ok: r.ok, data: data }; });
            }).then(function (res) {
                if (!res.ok) { throw new Error(res.data.message || 'Request failed'); }
                aiText.textContent = res.data.text;           // textContent: the model's output is never treated as HTML
                if (res.data.source === 'builtin') {
                    // The AI did not answer: show the built-in text and stop offering a button that cannot help.
                    aiNote.textContent = 'AI is not available right now, so this is the built-in explanation.';
                    aiButton.hidden = true;
                } else {
                    aiNote.textContent = 'Written by ' + res.data.source + '. AI can make mistakes, so check it against the lessons.';
                }
            }).catch(function (e) {
                aiNote.textContent = e.message || 'Could not get an explanation, try again.';
            }).then(function () { aiButton.disabled = false; });
        });
    }

    function check() {
        var answer = currentAnswer();
        if (!answer.trim()) {
            verdict.hidden = false;
            verdict.className = 'drill-verdict';
            verdict.querySelector('.verdict-line').textContent = 'Type or choose an answer first.';
            verdict.querySelector('.verdict-correct').textContent = '';
            verdict.querySelector('.verdict-why').textContent = '';
            return;
        }
        button.disabled = true;
        var headers = { 'Content-Type': 'application/json' };
        if (csrfToken && csrfHeader) {
            headers[csrfHeader] = csrfToken;
        }
        fetch('/api/drills/' + box.dataset.id + '/check', {
            method: 'POST', headers: headers, credentials: 'same-origin', body: JSON.stringify({ answer: answer })
        }).then(function (r) {
            if (!r.ok) { throw new Error('HTTP ' + r.status); }
            return r.json();
        }).then(function (data) {
            show(data.correct, data);
            if (data.correct) { if (aiBox) { aiBox.hidden = true; } } else { offerAi(answer); }
            if (window.DevVaultProgress) {
                window.DevVaultProgress.saveResult('drill', box.dataset.id, box.dataset.title, box.dataset.url,
                    data.correct ? 'right' : 'wrong', true).catch(function () { /* progress is optional */ });
            }
        }).catch(function () {
            verdict.hidden = false;
            verdict.className = 'drill-verdict';
            verdict.querySelector('.verdict-line').textContent = 'Could not check the answer, try again.';
        }).then(function () { button.disabled = false; });
    }

    button.addEventListener('click', check);
    var input = document.getElementById('answer-input');
    if (input) {
        input.addEventListener('keydown', function (e) { if (e.key === 'Enter') { check(); } });
    }
})();
