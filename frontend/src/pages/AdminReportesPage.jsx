import { useState, useEffect } from 'react'
import { reportesApi, descargasApi, usuariosApi, recursosApi } from '../api'
import { Spinner, StatCard } from '../components/ui'

function accionIcon(tipo = '') {
  if (tipo.includes('DESCARGA')) return '⬇️'
  if (tipo.includes('RECURSO'))  return '📚'
  if (tipo.includes('USUARIO'))  return '👤'
  if (tipo.includes('LOGIN'))    return '🔐'
  return '📋'
}

export default function AdminReportesPage() {
  const [stats, setStats]             = useState({})
  const [actividad, setActividad]     = useState([])
  const [topDescargas, setTopDescargas] = useState([])
  const [loading, setLoading]         = useState(true)

  useEffect(() => {
    Promise.allSettled([
      reportesApi.recientes(),
      descargasApi.top(),
      usuariosApi.listar(),
      recursosApi.listar({}),
      descargasApi.recientes(),
    ]).then(([actRes, topRes, usuRes, recRes, descRes]) => {
      if (actRes.status === 'fulfilled') {
        const d = actRes.value.data?.datos
        setActividad(Array.isArray(d) ? d : [])
      }
      if (topRes.status === 'fulfilled') {
        const d = topRes.value.data?.datos
        setTopDescargas(Array.isArray(d) ? d : [])
      }

      const totalUsuarios = usuRes.status === 'fulfilled'
        ? (usuRes.value.data?.datos?.length || 0) : 0
      const recData = recRes.status === 'fulfilled' ? recRes.value.data?.datos : null
      const totalRecursos = Array.isArray(recData) ? recData.length : (recData?.content?.length || 0)
      const descData = descRes.status === 'fulfilled' ? descRes.value.data?.datos : null
      const totalDescargas = Array.isArray(descData) ? descData.length : 0

      setStats({ totalUsuarios, totalRecursos, totalDescargas })
    }).finally(() => setLoading(false))
  }, [])

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>

  return (
    <div className="fade-in">
      <div className="mb-6">
        <h1 className="page-title">Reportes</h1>
        <p className="text-ink/40 text-sm">Panel general del sistema</p>
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        <StatCard icon="👥" label="Usuarios"            value={stats.totalUsuarios}  color="ink"  />
        <StatCard icon="📚" label="Recursos"            value={stats.totalRecursos}  color="gold" />
        <StatCard icon="⬇️" label="Descargas recientes" value={stats.totalDescargas} color="green"/>
        <StatCard icon="📋" label="Eventos registrados" value={actividad.length}     color="blue" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div>
          <h2 className="section-title">Recursos más descargados</h2>
          {topDescargas.length === 0 ? (
            <p className="text-ink/40 text-sm py-4">Sin datos aún</p>
          ) : (
            <div className="space-y-2">
              {topDescargas.slice(0, 8).map((d, i) => (
                <div key={d.recursoId || i} className="card py-3 flex items-center gap-3">
                  <span className="w-6 h-6 rounded-full bg-gold/10 text-gold-dark text-xs font-bold flex items-center justify-center flex-shrink-0">
                    {i + 1}
                  </span>
                  <span className="flex-1 text-sm text-ink font-medium truncate">
                    {d.tituloRecurso || `Recurso #${d.recursoId}`}
                  </span>
                  <span className="text-xs text-ink/40 flex-shrink-0">{d.totalDescargas} desc.</span>
                </div>
              ))}
            </div>
          )}
        </div>

        <div>
          <h2 className="section-title">Actividad reciente</h2>
          {actividad.length === 0 ? (
            <p className="text-ink/40 text-sm py-4">Sin actividad registrada</p>
          ) : (
            <div className="space-y-2">
              {actividad.slice(0, 8).map((a, i) => (
                <div key={a.id || i} className="card py-3 flex items-start gap-3">
                  <span className="text-base mt-0.5">{accionIcon(a.tipoEvento)}</span>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm text-ink font-medium">{a.tipoEvento}</p>
                    <p className="text-xs text-ink/40 truncate">{a.detalle}</p>
                    {a.ocurridoEn && (
                      <p className="text-xs text-ink/30 mt-0.5">
                        {new Date(a.ocurridoEn).toLocaleDateString('es-CO', { dateStyle: 'short' })}
                      </p>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
