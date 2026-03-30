import { useState } from "react";
import { useTranslation } from "react-i18next";
import axios from "axios";

const api = axios.create({ baseURL: import.meta.env.VITE_API_URL ?? "/api" });

const metrics = [
  { label: "Active members", value: "1,248" },
  { label: "Collected this month", value: "INR 3.8L" },
  { label: "Avg compliance", value: "84%" },
  { label: "Escalations", value: "17" }
];

const plans = [
  { time: "6:00", title: "Strength block", detail: "Squat, bench, accessory circuit" },
  { time: "13:00", title: "Regional nutrition", detail: "Poha, dal-rice, paneer bhurji, fruit" },
  { time: "19:00", title: "Recovery walk", detail: "20 mins with mobility cooldown" }
];

export default function App() {
  const { t, i18n } = useTranslation();
  const [chat, setChat] = useState("");
  const [answer, setAnswer] = useState("Ask for a workout tweak, Marathi meal option, or motivation message.");

  async function askCoach() {
    try {
      const response = await api.post("/ai/chat", {
        language: i18n.language,
        region: "Maharashtra",
        prompt: chat
      });
      setAnswer(response.data.content);
    } catch {
      setAnswer("AI service is not available yet. The backend is ready for a Groq-compatible model once API keys are configured.");
    }
  }

  return (
    <div className="page-shell">
      <header className="hero">
        <div>
          <p className="eyebrow">Production-ready gym operations suite</p>
          <h1>{t("title")}</h1>
          <p>{t("welcome")}</p>
        </div>
        <select value={i18n.language} onChange={(e) => i18n.changeLanguage(e.target.value)}>
          <option value="en">English</option>
          <option value="hi">Hindi</option>
          <option value="mr">Marathi</option>
        </select>
      </header>

      <section className="metric-grid">
        {metrics.map((metric) => (
          <article key={metric.label} className="card metric-card">
            <span>{metric.label}</span>
            <strong>{metric.value}</strong>
          </article>
        ))}
      </section>

      <main className="dashboard-grid">
        <section className="card schedule-card">
          <h2>{t("plans")}</h2>
          {plans.map((plan) => (
            <div className="timeline-row" key={plan.time}>
              <strong>{plan.time}</strong>
              <div>
                <h3>{plan.title}</h3>
                <p>{plan.detail}</p>
              </div>
            </div>
          ))}
        </section>

        <section className="card">
          <h2>{t("progress")}</h2>
          <div className="graph-placeholder">
            <div className="bar" style={{ height: "68%" }} />
            <div className="bar" style={{ height: "72%" }} />
            <div className="bar" style={{ height: "81%" }} />
            <div className="bar" style={{ height: "77%" }} />
            <div className="bar accent" style={{ height: "90%" }} />
          </div>
          <p>Weight trend, rep PRs, macro adherence, and stall detection can be wired to the progress service endpoints.</p>
        </section>

        <section className="card">
          <h2>{t("payments")}</h2>
          <ul className="payment-list">
            <li><span>Premium membership</span><strong>Paid</strong></li>
            <li><span>Trainer review add-on</span><strong>Pending</strong></li>
            <li><span>Invoice export</span><strong>CSV ready</strong></li>
          </ul>
        </section>

        <section className="card chat-card">
          <h2>{t("chat")}</h2>
          <textarea value={chat} onChange={(e) => setChat(e.target.value)} placeholder="Ask in English, Hindi, or Marathi" />
          <button onClick={askCoach}>Send</button>
          <div className="response-box">{answer}</div>
        </section>

        <section className="card">
          <h2>{t("trainers")}</h2>
          <p>Assigned client visibility, stalled-progress escalation, and exportable reporting are surfaced through the gateway-backed admin/trainer flows.</p>
          <div className="pill-row">
            <span>Client roster</span>
            <span>Payment view</span>
            <span>Progress exports</span>
            <span>AI corrections</span>
          </div>
        </section>
      </main>
    </div>
  );
}


