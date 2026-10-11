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
