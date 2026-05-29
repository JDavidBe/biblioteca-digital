import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { descargasApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { Spinner, EmptyState } from '../components/ui'

export default function MisDescargasPage() {
  const { usuario }               = useAuth()
  const [descargas, setDescargas] = useState([])
  const [loading, setLoading]     = useState(true)

  useEffect(() => {
    if (!usuario?.id) { setLoading(false); return }
    descargasApi.porUsuario(usuario.id)
      .then(({ data }) => setDescargas(Array.isArray(data?.datos) ? data.datos : []))
      .catch(() => setDescargas([]))
      .finally(() => setLoading(false))
  }, [usuario?.id])

  return (
    <div className="fade-in">
      <div className="mb-6">
        <h1 className="page-title">Mis Descargas</h1>
        <p className="text-ink/40 text-sm">Historial de recursos que has descargado</p>
      </div>

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : descargas.length === 0 ? (
        <EmptyState icon="⬇️" title="Sin descargas aún" desc="Explora el catálogo y descarga recursos" />
      ) : (
        <div className="space-y-3">
          {descargas.map(d => (
            <div key={d.id} className="card flex items-center gap-4">
              <span className="text-2xl">📄</span>
              <div className="flex-1 min-w-0">
                <Link to={`/app/recursos/${d.recursoId}`}
                  className="font-semibold text-ink hover:text-gold-dark transition-colors block truncate">
                  {d.tituloRecurso || `Recurso #${d.recursoId}`}
                </Link>
                <p className="text-xs text-ink/40 mt-0.5">
                  {d.descargadoEn
                    ? new Date(d.descargadoEn).toLocaleDateString('es-CO', { dateStyle: 'medium' })
                    : ''}
                </p>
              </div>
              <Link to={`/app/recursos/${d.recursoId}`} className="text-xs text-gold-dark hover:underline flex-shrink-0">
                Ver →
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
