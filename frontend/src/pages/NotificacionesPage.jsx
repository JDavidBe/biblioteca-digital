import { useState, useEffect } from 'react'
import { notificacionesApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { Spinner, EmptyState } from '../components/ui'

const tipoIcon = t => ({ BIENVENIDA: '👋', NUEVO_RECURSO: '📚', DESCARGA: '⬇️' }[t] || '🔔')
const tipoColor = t => ({ BIENVENIDA: 'bg-blue-50 text-blue-700', NUEVO_RECURSO: 'bg-gold/10 text-gold-dark', DESCARGA: 'bg-emerald-50 text-emerald-700' }[t] || 'bg-cream-dark text-ink')

export default function NotificacionesPage() {
  const { usuario } = useAuth()
  const [notifs, setNotifs] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    notificacionesApi.porUsuario(usuario?.id)
      .then(({ data }) => setNotifs(Array.isArray(data?.datos) ? data.datos : []))
      .catch(() => setNotifs([]))
      .finally(() => setLoading(false))
  }, [])

  return (
    <div className="fade-in">
      <div className="mb-6">
        <h1 className="page-title">Notificaciones</h1>
        <p className="text-ink/40 text-sm">Tus notificaciones recientes</p>
      </div>

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : notifs.length === 0 ? (
        <EmptyState icon="🔔" title="Sin notificaciones" desc="Te notificaremos cuando haya novedades" />
      ) : (
        <div className="space-y-3">
          {notifs.map(n => (
            <div key={n.id} className={`card flex items-start gap-3 ${!n.enviada ? 'border-l-4 border-l-gold' : ''}`}>
              <span className={`w-8 h-8 rounded-lg flex items-center justify-center text-sm flex-shrink-0 ${tipoColor(n.tipo)}`}>
                {tipoIcon(n.tipo)}
              </span>
              <div className="flex-1 min-w-0">
                <p className="font-semibold text-ink text-sm">{n.asunto}</p>
                <p className="text-xs text-ink/50 mt-0.5">{n.mensaje}</p>
                {n.creadaEn && (
                  <p className="text-xs text-ink/30 mt-1">
                    {new Date(n.creadaEn).toLocaleDateString('es-CO', { dateStyle: 'medium' })}
                  </p>
                )}
              </div>
              {!n.enviada && <span className="w-2 h-2 bg-gold rounded-full flex-shrink-0 mt-2" />}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
