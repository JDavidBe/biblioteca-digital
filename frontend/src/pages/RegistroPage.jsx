import { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { authApi, notificacionesApi } from '../api'
import axios from 'axios'
import { Spinner, Alert } from '../components/ui'

const ROLES = ['ESTUDIANTE', 'DOCENTE']

export default function RegistroPage() {
  const [correo, setCorreo]       = useState('')
  const [password, setPassword]   = useState('')
  const [confirmar, setConfirmar] = useState('')
  const [rol, setRol]             = useState('ESTUDIANTE')
  const [error, setError]         = useState('')
  const [loading, setLoading]     = useState(false)
  const navigate = useNavigate()

  const handleSubmit = async e => {
    e.preventDefault()
    setError('')
    if (password !== confirmar) { setError('Las contraseñas no coinciden'); return }
    if (password.length < 6)    { setError('La contraseña debe tener al menos 6 caracteres'); return }
    setLoading(true)
    try {
      await authApi.registro(correo, password, rol)

      const { data: loginData } = await authApi.login(correo, password)
      const tk  = loginData.datos.token
      const uid = loginData.datos.id

      try {
        await axios.post(
          `${import.meta.env.VITE_API_USUARIOS_URL}/api/usuarios`,
          {
            nombre:      correo.split('@')[0],
            correo,
            institucion: 'N/A',
            grado:       'N/A',
            rol,
          },
          { headers: { Authorization: `Bearer ${tk}` } }
        )
      } catch (perfilErr) {
        console.warn('No se pudo crear perfil en ms-usuarios:', perfilErr)
      }

      notificacionesApi.bienvenida({
  correo:    'jdavidbernalb@gmail.com',
  telefono:  '+573506595077',
  usuarioId: uid || 0,
  nombre:    correo.split('@')[0],
}).catch(() => {})

notificacionesApi.bienvenida({
  correo:    'johanstevenalvarezrodriguez@gmail.com',
  telefono:  '+573132923653',
  usuarioId: uid || 0,
  nombre:    correo.split('@')[0],
}).catch(() => {})

      navigate('/login', { state: { mensaje: 'Cuenta creada. Inicia sesión.' } })
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al registrar. Intenta de nuevo.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-cream flex items-center justify-center p-8">
      <div className="w-full max-w-sm fade-in">
        <div className="mb-8">
          <Link to="/login" className="text-xs text-ink/40 hover:text-ink transition-colors">← Volver al inicio</Link>
          <h2 className="font-display text-3xl text-ink mt-4">Crear cuenta</h2>
          <p className="text-ink/50 text-sm mt-1">Únete a la biblioteca digital</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {error && <Alert type="error">{error}</Alert>}

          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">Correo electrónico</label>
            <input type="email" value={correo} onChange={e => setCorreo(e.target.value)}
              className="input-field" placeholder="tu@correo.com" required />
          </div>

          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">Contraseña</label>
            <input type="password" value={password} onChange={e => setPassword(e.target.value)}
              className="input-field" placeholder="Mínimo 6 caracteres" required />
          </div>

          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">Confirmar contraseña</label>
            <input type="password" value={confirmar} onChange={e => setConfirmar(e.target.value)}
              className="input-field" placeholder="Repite tu contraseña" required />
          </div>

          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">Rol</label>
            <select value={rol} onChange={e => setRol(e.target.value)} className="input-field">
              {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>

          <button type="submit" disabled={loading} className="btn-primary w-full justify-center py-3 mt-2">
            {loading ? <Spinner size="sm" /> : 'Crear cuenta'}
          </button>
        </form>

        <p className="text-center text-sm text-ink/50 mt-6">
          ¿Ya tienes cuenta?{' '}
          <Link to="/login" className="text-gold-dark font-medium hover:underline">Inicia sesión</Link>
        </p>
      </div>
    </div>
  )
}
