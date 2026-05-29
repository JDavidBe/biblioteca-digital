import { useState, useEffect, useCallback } from 'react'
import { Link } from 'react-router-dom'
import { recursosApi } from '../api'
import { Spinner, EmptyState, Estrellas, CategoriaBadge } from '../components/ui'

const AREAS = ['', 'MATEMATICAS', 'CIENCIAS', 'HISTORIA', 'LITERATURA', 'TECNOLOGIA', 'ARTE', 'OTRO']
const TIPOS = ['', 'PDF', 'VIDEO', 'ARTICULO', 'LIBRO', 'PRESENTACION', 'OTRO']

function tipoIcon(tipo) {
  return { PDF: '📄', VIDEO: '🎬', ARTICULO: '📰', LIBRO: '📖', PRESENTACION: '📊', OTRO: '📎' }[tipo] || '📎'
}

export default function RecursosPage() {
  const [recursos, setRecursos] = useState([])
  const [loading, setLoading]   = useState(true)
  const [busqueda, setBusqueda] = useState('')
  const [area, setArea]         = useState('')

  const cargar = useCallback(async () => {
    setLoading(true)
    try {
      const params = {}
      if (busqueda) params.q    = busqueda
      if (area)     params.area = area
      const { data } = await recursosApi.listar(params)
      const d = data?.datos
      setRecursos(Array.isArray(d) ? d : (d?.content || []))
    } catch {
      setRecursos([])
    } finally {
      setLoading(false)
    }
  }, [busqueda, area])

  useEffect(() => { cargar() }, [cargar])

  return (
    <div className="fade-in">
      <div className="mb-6">
        <h1 className="page-title">Catálogo de Recursos</h1>
        <p className="text-ink/40 text-sm">Explora el material académico disponible</p>
      </div>

      <div className="flex flex-wrap gap-3 mb-6">
        <input
          type="text"
          placeholder="Buscar por título..."
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
          className="input-field flex-1 min-w-48"
        />
        <select value={area} onChange={e => setArea(e.target.value)} className="input-field w-48">
          {AREAS.map(a => <option key={a} value={a}>{a || 'Todas las áreas'}</option>)}
        </select>
      </div>

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : recursos.length === 0 ? (
        <EmptyState icon="📭" title="Sin resultados" desc="Prueba ajustando los filtros de búsqueda" />
      ) : (
        <>
          <p className="text-xs text-ink/40 mb-4">
            {recursos.length} recurso{recursos.length !== 1 ? 's' : ''} encontrado{recursos.length !== 1 ? 's' : ''}
          </p>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {recursos.map(r => (
              <Link key={r.id} to={`/app/recursos/${r.id}`}
                className="card group block hover:border-gold/50 transition-all duration-200"
              >
                <div className="flex items-start justify-between mb-3">
                  <span className="text-3xl">{tipoIcon(r.tipo)}</span>
                  {(r.area || r.categoria) && <CategoriaBadge categoria={r.area || r.categoria} />}
                </div>
                <h3 className="font-display font-semibold text-ink group-hover:text-gold-dark transition-colors line-clamp-2 mb-1 leading-snug">
                  {r.titulo}
                </h3>
                <p className="text-xs text-ink/50 line-clamp-2 mb-3 leading-relaxed">{r.descripcion}</p>
                <div className="flex items-center justify-between pt-3 border-t border-cream-dark">
                  <Estrellas puntuacion={r.promedioValoracion || 0} small />
                  <span className="text-xs text-ink/30">{r.tipo}</span>
                </div>
              </Link>
            ))}
          </div>
        </>
      )}
    </div>
  )
}
