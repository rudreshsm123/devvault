# Application kit: DevVault for Rajesh S M

Personal notes, not part of the public repo (this file is untracked on purpose). Everything below is based on what the
project really does today. Where something has not been verified, it says so, because an interviewer will ask.

---

## 1. Positioning

**One line:** "DevVault is an interview-prep platform I built in Java and Spring Boot that decides what you should
practise next: it schedules reviews with spaced repetition, finds your weak topics, builds a daily plan and runs timed
mock interviews."

**Thirty seconds:** "Most prep sites give you a list of problems. I wanted one that notices what you get wrong and brings
it back at the right time. DevVault stores every answer, schedules reviews with Leitner boxes, shows weak topics and
streaks, builds a ten-question daily plan, and runs timed mock interviews that are graded on the server so the answers
can't be read from the page. It has an optional AI explainer with a safe fallback. It's deployed with Docker on Render,
tested with a GitHub Actions pipeline, and the architecture is written up in the repo."

Why this matters to a recruiter: it shows you can design a feature end to end (data model, rules, security, UI, tests,
deployment), not just solve puzzles.

---

## 2. Resume

### Project entry (pick 4 or 5 bullets, keep each to one line or two)

**DevVault: adaptive interview-prep platform** | Java 21, Spring Boot, Spring Security, JPA/Hibernate, MySQL/H2,
Thymeleaf, Docker, GitHub Actions | [live demo link] | [GitHub link]

- Built a spaced-repetition engine (Leitner boxes, 1 to 32 day intervals) and a weak-topic dashboard with streaks and a
  12-week heatmap, driven by an append-only attempt log.
- Designed a daily plan generator that mixes due reviews, weak-topic questions and least-practised topics; the plan is
  persisted per day so it stays stable, with a unique key to handle two tabs opening it at once.
- Implemented timed mock interviews with server-side grading and clock: correct answers never reach the browser, late
  submissions are rejected after a grace period, and finishing twice is idempotent.
- Added an AI explainer behind a provider interface (Claude via the Anthropic Java SDK, Gemini or Groq via an
  OpenAI-compatible client, local Ollama) with per-user rate limiting, prompt-injection fencing and a built-in fallback so
  the feature never breaks the page.
- Hardened sign-in with a sliding-window login throttle (per address and username, plus a per-address cap) that runs
  before the password check, so lockouts leak nothing and a stranger can't lock the real owner out.
- Set up CI with GitHub Actions (JDK 21, MySQL service container) running 190+ unit, controller and integration tests, and
  deployed a multi-stage Docker image to Render.

### Skills section (only what the project really uses)

Java 21, Spring Boot, Spring MVC, Spring Security (BCrypt, CSRF, form login), Spring Data JPA, Hibernate, MySQL, H2,
REST APIs, Thymeleaf, JavaScript, JUnit 5, Mockito, MockMvc, Maven, Docker, GitHub Actions, Git, Mermaid/architecture docs.

### Do not claim

- Real user numbers, traffic or "users love it". There is no usage data.
- That the hosted AI providers are production-proven. Verified so far: the Ollama path against a real local model, and all
  providers against fake servers. The hosted Gemini and Claude calls have not been run with a real key. Say "supports"
  and be ready to explain that.
- Production-grade security. Login throttling exists (in memory, per address), but there is no e-mail verification or
  password reset yet, and the throttle does not stop many addresses guessing at one account. Owning that is better than
  hiding it.
- Test coverage percentages (not measured).

---

## 3. Two-minute demo script

Practice this until it takes under two minutes. Open the live site **a few minutes before** the call: the free host sleeps
and the first load can take a minute or two.

| Time | Do | Say |
|---|---|---|
| 0:00 | Welcome page | "DevVault is an interview-prep platform. Instead of a static list it adapts to what you get wrong." |
| 0:15 | Register, open **Practice Drills** | "There are 10,000 auto-graded drills, generated deterministically from question families, so ids are stable." |
| 0:30 | Answer two drills, one wrong | "Wrong answers are scheduled to come back tomorrow, right ones after longer gaps: 1, 2, 4, 8, 16, 32 days." |
| 0:50 | **Explain my answer with AI** | "Optional, behind a provider interface. If the model fails or there is no key it falls back to the built-in explanation." |
| 1:05 | **Review & Insights** | "Weak topics need at least three answers under 70%, so one slip doesn't label a topic. Streaks and heatmap come from the attempt log." |
| 1:25 | **Daily Plan** | "Ten items: due reviews, weak-topic questions, and least-practised topics. It's saved per day so it doesn't shuffle while you work." |
| 1:40 | **Mock Interview**, start 5 questions | "Timed, graded on the server, answers are not in the page. Late submissions are ignored." |
| 1:55 | Open the README architecture link | "Everything is documented with diagrams, tests run in CI, and it is deployed with Docker." |

Tip: record this once as a screen video (Windows Game Bar `Win+G`) and put the link at the top of the README and on
LinkedIn. Recruiters often watch 90 seconds of video before they open code.

---

## 4. Interview questions you should expect, with answers grounded in the code

**Why Leitner boxes and not SM-2?** Predictable, easy to explain and to unit test. It is six boxes in one pure class
(`ReviewScheduler`). SM-2 adapts per item but needs an ease factor and more state. If asked how I'd improve it: add an
ease factor per item and a lapse count.

**How do you stop users reading the answers in the mock interview?** The test page is built from a `Question` record that
has no answer or explanation field, so there is nothing to find in the HTML or network tab. Grading happens in
`InterviewService.finish`. The practice drill page does reveal the answer after a check, so the interview deliberately
does not reuse that endpoint.

**What if two tabs open the daily plan at the same time?** The plan rows have a unique key on user, day and position. One
save wins; the other catches the integrity violation and reads the saved plan back. There is a unit test for it.

**Why persist the daily plan instead of recomputing it?** Answering a question changes what the builder would choose (a
reviewed item is no longer due, a new one is no longer new), so the list would reshuffle mid-session.

**How does the AI feature fail safely?** Providers implement a one-method interface and return an empty string instead of
throwing; the explainer treats empty as "use the built-in explanation". Calls are rate limited per user, the learner's
text is capped, stripped of angle brackets and fenced as untrusted data, and the reply is rendered with `textContent`.

**How does your login throttling work, and what are its limits?** A filter before the password check counts failures in
a 15-minute sliding window: 5 per address and username, 30 per address overall. Keying on address plus username stops a
stranger from locking the owner out from elsewhere; blocked attempts are refused even with the right password and reveal
nothing. Limits: it is in memory (resets on restart, not shared between servers) and many different addresses can still
guess at one account. Next step would be Redis and per-account alerts or CAPTCHA.

**How do you protect against prompt injection?** I treat it as reduced, not solved. The learner's answer is fenced in
tags that the system prompt declares untrusted, it is length-limited and cannot close its own tag, and the output is
plain text only, so the worst case is a poor explanation, not an action.

**Why H2 on the public demo?** It costs nothing and gives a shareable link. The cost is that accounts vanish when the free
host restarts. Locally it uses MySQL with persistent data.

**What does your CI do and why a MySQL container?** Two tests run against the `local` profile, which uses MySQL, so the
workflow starts a throwaway MySQL 8.4 service container and writes the git-ignored properties file at build time.

**What would you do differently or next?** Flyway migrations instead of Hibernate auto-update, password reset, a shared
(Redis) login throttle, Testcontainers for integration tests, roles for content editing, and covering coding problems (not only
drills) in the plan and interview.

**Tell me about a bug you hit.** Real ones from this project you can tell honestly: Ollama's GPU backend crashed on my
MX230 ("unsupported toolchain") and I fixed it by running CPU-only and documenting it; and a mock-test stubbing problem
where Mockito's strict stubs failed my helper, fixed with lenient stubs for shared setup.

**How did you use AI tools?** Say it openly: "I used an AI coding assistant to speed up the work, and I reviewed, ran and
tested everything; I can explain every class." Then back it up: be able to open any file and walk through it. If you cannot
explain a part, read it before the interview.

---

## 5. Honest weak points and how to own them

- **Tested against fakes, not live hosted AI.** "The Ollama path is verified with a real local model; hosted providers
  are covered by tests against fake servers and I haven't run them with a paid key."
- **Demo data resets.** "That is a deliberate trade-off to keep hosting free; locally it uses MySQL."
- **No production users.** "It's a portfolio project; my goal was end-to-end ownership: design, security, tests, CI and
  deployment."
- **No experience.** "This project is my experience: I designed the data model, made trade-offs, wrote the tests and
  deployed it." Offer to walk through the code live.

---

## 6. Job search plan (you graduated July 2026)

**Reality check:** nobody can promise a job. What you control is volume, targeting and follow-through. Check each posting
for its graduation-year rule (many fresher roles accept the 2026 batch, some do not) so you don't spend time on ones you
can't get.

### Where to apply (don't limit yourself to startups)

1. IT services and consulting firms with fresher drives (large intake, structured process). Good for a first role.
2. Mid-size product companies and global capability centres in your city or remote-friendly ones.
3. Internships and contract-to-hire roles. "Experience required" is often flexible for these.
4. Startups, but only as part of the mix, and ask about funding and runway in the interview.
5. Off-campus drives and fresher hiring pages of companies (follow their career pages and fresher communities).

### Weekly rhythm

- 15 to 20 tailored applications a week, not 100 copy-paste ones. Change the first two lines of your cover note and the
  order of your resume bullets to match the role.
- 5 messages a week to engineers or alumni (see template). Referrals beat cold applications by a wide margin.
- 1 mock interview a week on DevVault itself, and review the misses in the Insights page. Use your own product.
- 2 hours a week on data structures and SQL; screening rounds still test them.

### Referral message (edit the brackets)

> Hi [Name], I'm Rajesh, a 2026 Computer Science graduate. I noticed [Company] is hiring for [role]. I built DevVault, a
> Java/Spring Boot interview-prep platform with spaced repetition and timed mock interviews ([link]), and I'd love to
> contribute to a team like yours. Could you share any advice on the role, or refer me if you think I'm a fit? Happy to
> send my resume. Thank you!

Keep it to five sentences. Never send the same message to ten people in one company.

### GitHub and LinkedIn checklist

- Pin the DevVault repo; add the live link and topics (`java`, `spring-boot`, `spaced-repetition`, `docker`).
- README top section: one-line pitch, live demo, screenshots, and the 2-minute video.
- Add screenshots of the **new** pages (Insights, Daily Plan, Mock Interview). The README screenshots are older.
- LinkedIn headline: "Java / Spring Boot developer | Built DevVault, an adaptive interview-prep platform".
- Add the project under Projects with the same bullets as the resume.

### 30-day plan

| Week | Focus |
|---|---|
| 1 | Add screenshots and the demo video; polish the resume; set up a tracking sheet (company, role, date, status). Apply to 15 roles. |
| 2 | Send 10 referral messages; practise the demo script and the Q&A above; one mock interview a day on DevVault. |
| 3 | Login throttling is done: practise explaining it. Pick the next visible improvement (password reset or Flyway); keep applying. |
| 4 | Review responses, tighten the resume against the roles that replied, ask for feedback after each rejection. |

---

## 7. Before you send the first application

- [ ] Open the live demo once; confirm it loads and the Mock Interview works after registering.
- [ ] Confirm the README CI badge is green and `docs/ARCHITECTURE.md` diagrams render on GitHub.
- [ ] Pick one feature and read its code until you can explain it without notes (suggestion: `ReviewScheduler`,
      `PlanBuilder`, `InterviewService.finish`).
- [ ] Resume is one page, with the project bullets and the live link.
- [ ] Decide your answer to "Did you use AI to build this?" (see section 4) and keep it consistent.
