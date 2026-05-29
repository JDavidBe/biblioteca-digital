import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { recursosApi, usuariosApi, descargasApi, valoracionesApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { StatCard, Spinner, Estrellas } from '../components/ui'

function tipoIcon(tipo) {
  return { PDF: '📄', VIDEO: '🎬', ARTICULO: '📰', LIBRO: '📖', PRESENTACION: '📊', OTRO: '📎' }[tipo] || '📎'
}

export default function HomePage() {
  const { usuario, esAdmin } = useAuth()
  const [stats, setStats]       = useState({})
  const [recientes, setRecientes] = useState([])
  const [loading, setLoading]   = useState(true)

  useEffect(() => {
    const cargar = async () => {
      try {
        const promises = [recursosApi.listar({})]
        if (esAdmin()) {
          promises.push(usuariosApi.listar(), descargasApi.recientes())
        }
        const results = await Promise.allSettled(promises)

        const recRes = results[0]
        let recursosLista = []
        if (recRes.status === 'fulfilled') {
          const d = recRes.value.data?.datos
          recursosLista = Array.isArray(d) ? d : (d?.content || [])
          setRecientes(recursosLista.slice(0, 6))
          setStats(prev => ({ ...prev, totalRecursos: recursosLista.length }))
        }

        if (esAdmin()) {
          const usuRes = results[1]
          const descRes = results[2]
          if (usuRes?.status === 'fulfilled') {
            const d = usuRes.value.data?.datos
            setStats(prev => ({ ...prev, totalUsuarios: Array.isArray(d) ? d.length : 0 }))
          }
          if (descRes?.status === 'fulfilled') {
            const d = descRes.value.data?.datos
            setStats(prev => ({ ...prev, totalDescargas: Array.isArray(d) ? d.length : 0 }))
          }

          // Cargar valoraciones de todos los recursos
          if (recursosLista.length > 0) {
            const valPromises = recursosLista.map(r =>
              valoracionesApi.porRecurso(r.id).catch(() => ({ data: { datos: [] } }))
            )
            const valResults = await Promise.allSettled(valPromises)
            let totalVal = 0
            valResults.forEach(res => {
              if (res.status === 'fulfilled') {
                const d = res.value.data?.datos
                totalVal += Array.isArray(d) ? d.length : 0
              }
            })
            setStats(prev => ({ ...prev, totalValoraciones: totalVal }))
          } else {
            setStats(prev => ({ ...prev, totalValoraciones: 0 }))
          }
        }
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [])

  const hora = new Date().getHours()
  const saludo = hora < 12 ? 'Buenos días' : hora < 18 ? 'Buenas tardes' : 'Buenas noches'
  const nombre = usuario?.nombre || usuario?.correo?.split('@')[0] || 'Usuario'

  return (
    <div className="fade-in">
      <div className="mb-8">
        <p className="text-ink/40 text-sm">{saludo}</p>
        <h1 className="font-display text-3xl text-ink mt-0.5">{nombre} 👋</h1>
      </div>

      {esAdmin() && (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
          <StatCard icon="👥" label="Usuarios registrados"  value={stats.totalUsuarios}  color="ink"  />
          <StatCard icon="📚" label="Recursos publicados"   value={stats.totalRecursos}  color="gold" />
          <StatCard icon="⬇️" label="Descargas recientes"   value={stats.totalDescargas} color="green"/>
          <StatCard icon="⭐" label="Valoraciones"          value={stats.totalValoraciones} color="blue" />
        </div>
      )}

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <Link to="/app/recursos" className="card group cursor-pointer border-l-4 border-l-gold">
          <div className="text-2xl mb-2">📚</div>
          <h3 className="font-semibold text-ink group-hover:text-gold-dark transition-colors">Explorar recursos</h3>
          <p className="text-xs text-ink/40 mt-1">Navega el catálogo completo</p>
        </Link>
        {(usuario?.rol === 'DOCENTE' || esAdmin()) && (
          <Link to="/app/mis-recursos" className="card group cursor-pointer border-l-4 border-l-ink-muted">
            <div className="text-2xl mb-2">📝</div>
            <h3 className="font-semibold text-ink group-hover:text-gold-dark transition-colors">Publicar recurso</h3>
            <p className="text-xs text-ink/40 mt-1">Comparte material académico</p>
          </Link>
        )}
        <Link to="/app/mis-descargas" className="card group cursor-pointer border-l-4 border-l-emerald-400">
          <div className="text-2xl mb-2">⬇️</div>
          <h3 className="font-semibold text-ink group-hover:text-gold-dark transition-colors">Mis descargas</h3>
          <p className="text-xs text-ink/40 mt-1">Historial de recursos descargados</p>
        </Link>
      </div>

      <div>
        <div className="flex items-center justify-between mb-4">
          <h2 className="section-title mb-0">Recursos recientes</h2>
          <Link to="/app/recursos" className="text-xs text-gold-dark hover:underline">Ver todos →</Link>
        </div>

        {loading ? (
          <div className="flex justify-center py-12"><Spinner size="lg" /></div>
        ) : recientes.length === 0 ? (
          <p className="text-ink/40 text-sm py-8 text-center">No hay recursos publicados aún.</p>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {recientes.map(r => (
              <Link key={r.id} to={`/app/recursos/${r.id}`} className="card group block">
                <div className="flex items-start justify-between mb-2">
                  <span className="text-2xl">{tipoIcon(r.tipo)}</span>
                  <span className="badge bg-ink/10 text-ink text-xs">{r.area || r.categoria}</span>
                </div>
                <h3 className="font-semibold text-ink text-sm leading-snug group-hover:text-gold-dark transition-colors line-clamp-2 mb-1">
                  {r.titulo}
                </h3>
                <p className="text-xs text-ink/40 line-clamp-2 mb-3">{r.descripcion}</p>
                <div className="flex items-center gap-2">
                  <Estrellas puntuacion={r.promedioValoracion || 0} small />
                  <span className="text-xs text-ink/30">{r.tipo}</span>
                </div>
              </Link>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
