import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { valoracionesApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { Spinner, EmptyState, Estrellas, Alert } from '../components/ui'

export default function MisValoracionesPage() {
  const { usuario } = useAuth()
  const [valoraciones, setValoraciones] = useState([])
  const [loading, setLoading] = useState(true)
  const [msg, setMsg] = useState('')

  const cargar = () => {
    valoracionesApi.porUsuario(usuario?.id)
      .then(({ data }) => setValoraciones(Array.isArray(data?.datos) ? data.datos : []))
      .catch(() => setValoraciones([]))
      .finally(() => setLoading(false))
  }

  useEffect(() => { cargar() }, [])

  const handleEliminar = async (id) => {
    if (!confirm('¿Eliminar esta valoración?')) return
    try {
      await valoracionesApi.eliminar(id)
      setMsg('✓ Valoración eliminada')
      cargar()
    } catch { setMsg('Error al eliminar') }
  }

  return (
    <div className="fade-in">
      <div className="mb-6">
        <h1 className="page-title">Mis Valoraciones</h1>
        <p className="text-ink/40 text-sm">Recursos que has calificado</p>
      </div>

      {msg && <div className="mb-4"><Alert type={msg.startsWith('✓') ? 'success' : 'error'}>{msg}</Alert></div>}

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : valoraciones.length === 0 ? (
        <EmptyState icon="⭐" title="Sin valoraciones aún" desc="Valora recursos para verlos aquí" />
      ) : (
        <div className="space-y-3">
          {valoraciones.map(v => (
            <div key={v.id} className="card flex items-center gap-4">
              <Estrellas puntuacion={v.puntuacion} />
              <div className="flex-1 min-w-0">
                <Link to={`/app/recursos/${v.recursoId}`} className="font-semibold text-ink hover:text-gold-dark transition-colors block truncate">
                  Recurso #{v.recursoId}
                </Link>
                {v.comentario && <p className="text-xs text-ink/50 mt-0.5 truncate">"{v.comentario}"</p>}
              </div>
              <button
                onClick={() => handleEliminar(v.id)}
                className="text-xs text-red-400 hover:text-red-600 transition-colors flex-shrink-0"
              >
                Eliminar
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
