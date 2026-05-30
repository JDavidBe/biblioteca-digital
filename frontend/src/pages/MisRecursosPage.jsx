import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { recursosApi, notificacionesApi, reportesApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { Spinner, Alert, EmptyState, Modal, CategoriaBadge } from '../components/ui'

function tipoIcon(tipo) {
  const icons = { PDF: '📄', VIDEO: '🎬', ARTICULO: '📰', LIBRO: '📖', PRESENTACION: '📊', OTRO: '📎' }
  return icons[tipo] || '📎'
}

const AREAS = ['MATEMATICAS', 'CIENCIAS', 'HISTORIA', 'LITERATURA', 'TECNOLOGIA', 'ARTE', 'OTRO']
const TIPOS = ['PDF', 'VIDEO', 'ARTICULO', 'LIBRO', 'PRESENTACION', 'OTRO']

const emptyForm = { titulo: '', descripcion: '', area: 'MATEMATICAS', grado: '', tipo: 'PDF' }

export default function MisRecursosPage() {
  const { usuario } = useAuth()
  const [recursos, setRecursos] = useState([])
  const [loading, setLoading] = useState(true)
  const [modal, setModal] = useState(false)
  const [form, setForm] = useState(emptyForm)
  const [archivo, setArchivo] = useState(null)
  const [publicando, setPublicando] = useState(false)
  const [error, setError] = useState('')
  const [msg, setMsg] = useState('')

  const cargar = async () => {
    try {
      const { data } = await recursosApi.misRecursos(usuario?.id)
      setRecursos(Array.isArray(data?.datos) ? data.datos : [])
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { cargar() }, [])

  const handlePublicar = async e => {
    e.preventDefault()
    if (!archivo) { setError('Debes adjuntar un archivo'); return }
    setPublicando(true)
    setError('')
    try {
      const datos = {
        ...form,
        subidoPorId: usuario?.id,
        subidoPorCorreo: usuario?.correo
      }
      const { data } = await recursosApi.publicar(datos, archivo)
      const recursoId = data?.datos?.id || 0

      notificacionesApi.nuevoRecurso({
        correo:        'jdavidbernalb@gmail.com',
    telefono:      '+573506595077',
  usuarioId:     usuario?.id,
  tituloRecurso: (usuario?.correo || 'alguien') + ' publicó: ' + form.titulo,
}).catch(() => {})

      notificacionesApi.nuevoRecurso({
  correo:        'johanstevenalvarezrodriguez@gmail.com',
  telefono:      '+573132923653',
  usuarioId:     usuario?.id,
  tituloRecurso: (usuario?.correo || 'alguien') + ' publicó: ' + form.titulo,
}).catch(() => {})

      reportesApi.registrar({
        tipoEvento:    'PUBLICACION',
        entidadId:     recursoId,
        entidadTipo:   'RECURSO',
        usuarioId:     usuario?.id || 0,
        usuarioCorreo: usuario?.correo,
        detalle:       `Recurso publicado: ${form.titulo}`,
      }).catch(() => {})

      setModal(false)
      setForm(emptyForm)
      setArchivo(null)
      setMsg('✓ Recurso publicado correctamente')
      cargar()
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al publicar el recurso')
    } finally {
      setPublicando(false)
    }
  }

  const handleEliminar = async (id, titulo) => {
    if (!confirm('¿Eliminar este recurso?')) return
    try {
      await recursosApi.eliminar(id)
      reportesApi.registrar({
        tipoEvento:    'ELIMINACION',
        entidadId:     id,
        entidadTipo:   'RECURSO',
        usuarioId:     usuario?.id || 0,
        usuarioCorreo: usuario?.correo,
        detalle:       `Recurso eliminado: ${titulo}`,
      }).catch(() => {})
      setMsg('✓ Recurso eliminado')
      cargar()
    } catch { setError('Error al eliminar') }
  }

  return (
    <div className="fade-in">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="page-title">Mis Publicaciones</h1>
          <p className="text-ink/40 text-sm">Recursos que has publicado</p>
        </div>
        <button onClick={() => setModal(true)} className="btn-primary">
          + Publicar recurso
        </button>
      </div>

      {error && <div className="mb-4"><Alert type="error">{error}</Alert></div>}
      {msg && <div className="mb-4"><Alert type="success">{msg}</Alert></div>}

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : recursos.length === 0 ? (
        <EmptyState icon="📝" title="Nada publicado aún"
          desc="Comparte material académico con la comunidad" />
      ) : (
        <div className="space-y-3">
          {recursos.map(r => (
            <div key={r.id} className="card flex items-center gap-4">
              <span className="text-2xl flex-shrink-0">{tipoIcon(r.tipo)}</span>
              <div className="flex-1 min-w-0">
                <Link to={`/app/recursos/${r.id}`} className="font-semibold text-ink hover:text-gold-dark transition-colors">
                  {r.titulo}
                </Link>
                <div className="flex gap-2 mt-1">
                  <CategoriaBadge categoria={r.area} />
                </div>
              </div>
              <button
                onClick={() => handleEliminar(r.id, r.titulo)}
                className="text-xs text-red-400 hover:text-red-600 transition-colors flex-shrink-0"
              >
                Eliminar
              </button>
            </div>
          ))}
        </div>
      )}

      <Modal open={modal} onClose={() => setModal(false)} title="Publicar nuevo recurso">
        <form onSubmit={handlePublicar} className="space-y-3">
          {error && <Alert type="error">{error}</Alert>}
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Título *</label>
            <input value={form.titulo} onChange={e => setForm({...form, titulo: e.target.value})}
              className="input-field" placeholder="Título del recurso" required />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Descripción</label>
            <textarea value={form.descripcion} onChange={e => setForm({...form, descripcion: e.target.value})}
              className="input-field resize-none" rows={3} placeholder="Descripción breve..." />
          </div>
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Área</label>
              <select value={form.area} onChange={e => setForm({...form, area: e.target.value})} className="input-field">
                {AREAS.map(a => <option key={a} value={a}>{a}</option>)}
              </select>
            </div>
            <div>
              <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Tipo</label>
              <select value={form.tipo} onChange={e => setForm({...form, tipo: e.target.value})} className="input-field">
                {TIPOS.map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </div>
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Grado / Nivel</label>
            <input value={form.grado} onChange={e => setForm({...form, grado: e.target.value})}
              className="input-field" placeholder="Ej: Universitario, Secundaria..." />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Archivo *</label>
            <input type="file" onChange={e => setArchivo(e.target.files[0])}
              className="input-field text-xs file:mr-3 file:py-1 file:px-3 file:rounded file:border-0 file:bg-ink file:text-cream file:text-xs cursor-pointer" required />
          </div>
          <button type="submit" disabled={publicando} className="btn-primary w-full justify-center">
            {publicando ? <Spinner size="sm" /> : 'Publicar recurso'}
          </button>
        </form>
      </Modal>
    </div>
  )
}
