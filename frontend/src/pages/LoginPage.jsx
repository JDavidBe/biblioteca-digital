import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Spinner, Alert } from '../components/ui'

export default function LoginPage() {
  const [correo, setCorreo] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(correo, password)
      navigate('/app')
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Credenciales incorrectas')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-ink flex">
      {/* Left panel */}
      <div className="hidden lg:flex lg:w-1/2 flex-col justify-between p-12 relative overflow-hidden">
        <div className="absolute inset-0 opacity-5"
          style={{backgroundImage:'repeating-linear-gradient(0deg,transparent,transparent 50px,#e2b04a 50px,#e2b04a 51px),repeating-linear-gradient(90deg,transparent,transparent 50px,#e2b04a 50px,#e2b04a 51px)'}}
        />
        <div className="relative">
          <h1 className="font-display text-4xl text-cream">Biblioteca</h1>
          <p className="text-gold tracking-widest uppercase text-sm mt-1">Digital Académica</p>
        </div>
        <div className="relative">
          <blockquote className="text-cream/60 font-display text-2xl leading-relaxed italic">
            "El conocimiento es el único bien que crece cuando se comparte."
          </blockquote>
        </div>
        <div className="relative flex gap-8 text-cream/30 text-sm">
          <span>7 microservicios</span>
          <span>·</span>
          <span>Recursos académicos</span>
          <span>·</span>
          <span>Acceso libre</span>
        </div>
      </div>

      {/* Right panel */}
      <div className="w-full lg:w-1/2 bg-cream flex items-center justify-center p-8">
        <div className="w-full max-w-sm fade-in">
          <div className="mb-8">
            <h2 className="font-display text-3xl text-ink">Bienvenido</h2>
            <p className="text-ink/50 text-sm mt-1">Ingresa a tu cuenta para continuar</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            {error && <Alert type="error">{error}</Alert>}

            <div>
              <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">
                Correo electrónico
              </label>
              <input
                type="email"
                value={correo}
                onChange={e => setCorreo(e.target.value)}
                className="input-field"
                placeholder="tu@correo.com"
                required
              />
            </div>

            <div>
              <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">
                Contraseña
              </label>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                className="input-field"
                placeholder="••••••••"
                required
              />
            </div>

            <button
              type="submit"
              disabled={loading}
              className="btn-primary w-full justify-center py-3 mt-2"
            >
              {loading ? <Spinner size="sm" /> : 'Iniciar sesión'}
            </button>
          </form>

          <p className="text-center text-sm text-ink/50 mt-6">
            ¿No tienes cuenta?{' '}
            <Link to="/registro" className="text-gold-dark font-medium hover:underline">
              Regístrate
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
