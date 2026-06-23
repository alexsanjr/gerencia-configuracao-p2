import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { register as registerService } from "../../services/registerService";
import NavBar from "../../components/NavBar/NavBar";
import "./Register.css";
import bkgRegister from "../../assets/bkglogin3.jpg";
import { login } from "../../services/loginService";
import { Icon } from "@iconify/react";

// Função para aplicar máscara de CPF
function maskCPF(value: string) {
    return value
        .replace(/\D/g, "")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d)/, "$1.$2")
        .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
        .slice(0, 14);
}

// Função para aplicar máscara de telefone
function maskPhone(value: string) {
    return value
        .replace(/\D/g, "")
        .replace(/^(\d{2})(\d)/g, "($1) $2")
        .replace(/(\d{5})(\d{1,4})$/, "$1-$2")
        .slice(0, 15);
}

export default function Register() {
  const [nome, setNome] = useState("");
  const [documento, setDocumento] = useState("");
  const [dataNascimento, setDataNascimento] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [telefone, setTelefone] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (localStorage.getItem("token")) {
      window.location.replace("/aluguel");
    }
  }, []);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError("");
    setSuccess(false);
    setLoading(true);
    try {
      const usuario = {
        nome,
        documento: documento.replace(/\D/g, ""), // envia só números
        dataNascimento: dataNascimento,
        email,
        password,
        telefone: telefone.replace(/\D/g, ""), // envia só números
        tipo: "CLIENTE",
      };

            await registerService(usuario);
            setSuccess(true);

      const { token } = await login(email, password);
      localStorage.setItem("token", token);
      navigate("/aluguel");
    } catch (err: any) {
      setError(err?.response?.data?.message || "Erro ao registrar. Tente novamente.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <NavBar />
      <div className="register-container">
        <div className="register-content">
          <div
            className="register-left"
            style={{ backgroundImage: `url(${bkgRegister})` }}
          >
            <div className="left-overlay">
              <div className="container-text">
                <div className="left-text">
                  <h2>Bem-vindo ao Você-Aluga!</h2>
                  <p>
                    Crie sua conta agora para acessar nossa frota de veículos e
                    aproveitar condições especiais em suas locações.
                  </p>
                </div>
                <div className="features-list">
                  <div className="feature-item">
                    <Icon icon="tabler:car" />
                    <span>Ampla frota de veículos</span>
                  </div>
                  <div className="feature-item">
                    <Icon icon="tabler:discount-check" />
                    <span>Preços competitivos</span>
                  </div>
                  <div className="feature-item">
                    <Icon icon="tabler:clock-24" />
                    <span>Atendimento 24 horas</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div className="register-right">
            <div className="register-box">
              <div className="register-header">
                <h2>Crie sua conta</h2>
                <p>Preencha seus dados para começar</p>
              </div>

              <form onSubmit={handleSubmit} autoComplete="off">
                <div className="form-group">
                  <label htmlFor="nome">Nome completo</label>
                  <div className="input-with-icon">
                    <input
                      type="text"
                      id="nome"
                      placeholder="Digite seu nome completo"
                      required
                      value={nome}
                      onChange={e => setNome(e.target.value.replace(/[^A-Za-zÀ-ÿ\s]/g, ""))}
                      maxLength={60}
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="documento">CPF</label>
                  <div className="input-with-icon">
                    <input
                      type="text"
                      id="documento"
                      placeholder="000.000.000-00"
                      required
                      value={maskCPF(documento)}
                      onChange={e => setDocumento(maskCPF(e.target.value))}
                      maxLength={14}
                      inputMode="numeric"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="dataNascimento">Data de nascimento</label>
                  <div className="input-with-icon">
                    <input
                      type="date"
                      id="dataNascimento"
                      required
                      value={dataNascimento}
                      onChange={e => setDataNascimento(e.target.value)}
                      max="2020-12-31"
                      min="1900-01-01"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="telefone">Telefone</label>
                  <div className="input-with-icon">
                    <input
                      type="tel"
                      id="telefone"
                      placeholder="(00) 00000-0000"
                      required
                      value={maskPhone(telefone)}
                      onChange={e => setTelefone(maskPhone(e.target.value))}
                      maxLength={15}
                      inputMode="numeric"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label htmlFor="email">E-mail</label>
                  <div className="input-with-icon">
                    <input
                      type="email"
                      id="email"
                      placeholder="Digite seu e-mail"
                      required
                      value={email}
                      onChange={e => setEmail(e.target.value.replace(/[^\w@.\-]/g, ""))}
                      maxLength={60}
                      autoComplete="email"
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
                      onChange={e => setPassword(e.target.value.replace(/\s/g, ""))}
                      minLength={6}
                      maxLength={32}
                      autoComplete="new-password"
                    />
                  </div>
                </div>

                {error && <div className="error-message">
                  <Icon icon="tabler:alert-circle" />
                  {error}
                </div>}
                {success && <div className="success-message">
                  <Icon icon="tabler:check" />
                  Cadastro realizado com sucesso! Redirecionando...
                </div>}

                <button type="submit" className="register-button" disabled={loading}>
                  {loading ? (
                    <div className="button-loading">
                      <Icon icon="tabler:loader-2" className="spin" />
                      <span>Registrando...</span>
                    </div>
                  ) : (
                    <>
                      <Icon icon="tabler:user-plus" />
                      <span>Criar conta</span>
                    </>
                  )}
                </button>
              </form>

              <div className="register-footer">
                <p className="signup-link">
                  Já possui uma conta? <a href="/login">Entrar</a>
                </p>
                <div className="extra-links">
                  <a href="#">Termos de uso</a>
                  <a href="#">Privacidade</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
