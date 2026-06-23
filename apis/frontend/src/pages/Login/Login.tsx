import { useState, useEffect } from "react";
import { login } from "../../services/loginService";
import { useNavigate } from "react-router-dom";
import NavBar from "../../components/NavBar/NavBar";
import "./Login.css";
import bkgLogin from "../../assets/bkglogin3.jpg";
import { parseJwt } from "../../utils/jwt";
import { Icon } from "@iconify/react";


export default function Login() {
  useEffect(() => {
    if (localStorage.getItem("token")) {
      window.location.replace("/aluguel");
    }
  }, []);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      const { token } = await login(email, password);
      localStorage.setItem("token", token);
      const payload = parseJwt(token);
      if (payload && payload.role === "ROLE_ADMININISTRADOR") {
        navigate("/adm");
      } else {
        navigate("/aluguel");
      }
    } catch (err: any) {
      setError("Usuário ou senha inválidos");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <NavBar />
      <div className="login-container">
        <div className="login-content">
          <div
            className="login-left"
            style={{ backgroundImage: `url(${bkgLogin})` }}
          >
            <div className="left-overlay">
              <div className="container-text">
                <div className="left-text">
                  <h2>Bem-vindo de volta!</h2>
                  <p>
                    Acesse sua conta para gerenciar suas reservas, visualizar veículos disponíveis
                    e aproveitar nossas ofertas exclusivas.
                  </p>
                </div>
                <div className="features-list">
                  <div className="feature-item">
                    <Icon icon="tabler:car" />
                    <span>Ampla frota de veículos</span>
                  </div>
                  <div className="feature-item">
                    <Icon icon="tabler:calendar-check" />
                    <span>Reservas simplificadas</span>
                  </div>
                  <div className="feature-item">
                    <Icon icon="tabler:shield-check" />
                    <span>Segurança garantida</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="login-right">
            <div className="login-box">
              <div className="login-header">
                <h2>Acesse sua conta</h2>
                <p>Entre com suas credenciais para continuar</p>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label htmlFor="email">E-mail</label>
                  <div className="input-with-icon">
                    <input
                      type="email"
                      id="email"
                      placeholder="Digite seu e-mail"
                      required
                      value={email}
                      onChange={e => setEmail(e.target.value)}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="password">Senha</label>
                  <div className="input-with-icon">
                    <input
                      type="password"
                      id="password"
                      placeholder="Digite sua senha"
                      required
                      value={password}
                      onChange={e => setPassword(e.target.value)}
                    />
                  </div>
                </div>

                {error && <div className="error-message">{error}</div>}

                <button type="submit" className="login-button">
                  {loading ? (
                    <div className="button-loading">
                      <Icon icon="tabler:loader-2" className="spin" />
                      <span>Entrando...</span>
                    </div>
                  ) : (
                    <>
                      <Icon icon="tabler:login" />
                      <span>Entrar</span>
                    </>
                  )}
                </button>
              </form>

              <div className="login-footer">
                <p className="signup-link">
                  Ainda não tem uma conta? <a href="/register">Cadastre-se agora</a>
                </p>
                <div className="extra-links">
                  <a href="#">Esqueci minha senha</a>
                  <a href="#">Preciso de ajuda</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
