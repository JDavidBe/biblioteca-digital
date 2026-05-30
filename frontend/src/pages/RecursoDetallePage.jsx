import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { recursosApi, valoracionesApi, descargasApi, notificacionesApi, reportesApi } from '../api'
import { useAuth } from '../context/AuthContext'
import { Spinner, Alert, Estrellas, CategoriaBadge, Modal } from '../components/ui'

function tipoIcon(tipo) {
  return { PDF: '📄', VIDEO: '🎬', ARTICULO: '📰', LIBRO: '📖', PRESENTACION: '📊', OTRO: '📎' }[tipo] || '📎'
}

export default function RecursoDetallePage() {
  const { id }         = useParams()
  const { usuario, esAdmin } = useAuth()
  const navigate       = useNavigate()

  const [recurso, setRecurso]               = useState(null)
  const [resumen, setResumen]               = useState(null)
  const [loading, setLoading]               = useState(true)
  const [error, setError]                   = useState('')
  const [msg, setMsg]                       = useState('')
  const [descargando, setDescargando]       = useState(false)
  const [cargandoPdf, setCargandoPdf]       = useState(false)
  const [pdfUrl, setPdfUrl]                 = useState(null)
  const [viendoPdf, setViendoPdf]           = useState(false)
  const [modalVal, setModalVal]             = useState(false)
  const [puntuacion, setPuntuacion]         = useState(5)
  const [comentario, setComentario]         = useState('')
  const [valorandoLoad, setValorandoLoad]   = useState(false)

  useEffect(() => {
    const cargar = async () => {
      try {
        const [rRes, vRes] = await Promise.allSettled([
          recursosApi.obtener(id),
          valoracionesApi.resumen(id),
        ])
        if (rRes.status === 'fulfilled') setRecurso(rRes.value.data?.datos)
        if (vRes.status === 'fulfilled') setResumen(vRes.value.data?.datos)
      } catch {
        setError('No se pudo cargar el recurso.')
      } finally {
        setLoading(false)
      }
    }
    cargar()
  }, [id])

  useEffect(() => () => { if (pdfUrl) URL.revokeObjectURL(pdfUrl) }, [pdfUrl])

  const registrarDescarga = async () => {
    await descargasApi.registrar({
      recursoId:     Number(id),
      usuarioId:     usuario?.id || 0,
      usuarioCorreo: usuario?.correo,
      tituloRecurso: recurso?.titulo,
    }).catch(() => {})
    notificacionesApi.descarga({
      correo:        'jdavidbernalb@gmail.com',
      telefono:      '+573506595077',
      usuarioId:     usuario?.id || 0,
      tituloRecurso: (usuario?.correo || 'alguien') + ' descargó: ' + recurso?.titulo,
    }).catch(() => {})
    reportesApi.registrar({
      tipoEvento:    'DESCARGA',
      entidadId:     Number(id),
      entidadTipo:   'RECURSO',
      usuarioId:     usuario?.id || 0,
      usuarioCorreo: usuario?.correo,
      detalle:       `Descarga de recurso: ${recurso?.titulo}`,
    }).catch(() => {})
  }

  const handleVerPdf = async () => {
    if (pdfUrl) { setViendoPdf(true); return }
    setCargandoPdf(true)
    try {
      const response = await recursosApi.obtenerBlob(id)
      const blob = new Blob([response.data], { type: 'application/pdf' })
      setPdfUrl(URL.createObjectURL(blob))
      setViendoPdf(true)
      await registrarDescarga()
    } catch {
      setError('Error al cargar el PDF.')
    } finally {
      setCargandoPdf(false)
    }
  }

  const handleDescargar = async () => {
    setDescargando(true)
    try {
      await recursosApi.descargar(id, recurso?.nombreArchivo || `recurso-${id}`)
      await registrarDescarga()
      setMsg('✓ Descarga iniciada')
    } catch {
      setError('Error al descargar el recurso.')
    } finally {
      setDescargando(false)
    }
  }

  const handleValorar = async () => {
    setValorandoLoad(true)
    try {
      await valoracionesApi.calificar({
        recursoId:     Number(id),
        usuarioId:     usuario?.id || 0,
        usuarioCorreo: usuario?.correo,
        puntuacion,
        comentario,
      })
      reportesApi.registrar({
        tipoEvento:    'VALORACION',
        entidadId:     Number(id),
        entidadTipo:   'RECURSO',
        usuarioId:     usuario?.id || 0,
        usuarioCorreo: usuario?.correo,
        detalle:       `Valoración de ${puntuacion} estrellas al recurso: ${recurso?.titulo}`,
      }).catch(() => {})
      const { data } = await valoracionesApi.resumen(id)
      setResumen(data?.datos)
      setModalVal(false)
      setComentario('')
      setMsg('✓ Valoración enviada')
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al valorar.')
    } finally {
      setValorandoLoad(false)
    }
  }

  const handleEliminar = async () => {
    if (!confirm('¿Eliminar este recurso? Esta acción no se puede deshacer.')) return
    try {
      await recursosApi.eliminar(id)
      reportesApi.registrar({
        tipoEvento:    'ELIMINACION',
        entidadId:     Number(id),
        entidadTipo:   'RECURSO',
        usuarioId:     usuario?.id || 0,
        usuarioCorreo: usuario?.correo,
        detalle:       `Recurso eliminado: ${recurso?.titulo}`,
      }).catch(() => {})
      navigate('/app/recursos')
    } catch {
      setError('Error al eliminar el recurso.')
    }
  }

  if (loading) return <div className="flex justify-center py-20"><Spinner size="lg" /></div>
  if (!recurso) return <Alert type="error">Recurso no encontrado.</Alert>

  const totalValoraciones = resumen?.total ?? resumen?.totalValoraciones ?? 0

  return (
    <div className="fade-in max-w-2xl">
      <button onClick={() => navigate(-1)} className="text-xs text-ink/40 hover:text-ink mb-6 inline-block">
        ← Volver
      </button>

      {error && <div className="mb-4"><Alert type="error">{error}</Alert></div>}
      {msg   && <div className="mb-4"><Alert type="success">{msg}</Alert></div>}

      <div className="card mb-6">
        <div className="flex items-start gap-4 mb-4">
          <span className="text-5xl">{tipoIcon(recurso.tipo)}</span>
          <div className="flex-1 min-w-0">
            <h1 className="font-display text-2xl text-ink leading-tight">{recurso.titulo}</h1>
            <div className="flex flex-wrap gap-2 mt-2">
              {(recurso.area || recurso.categoria) && <CategoriaBadge categoria={recurso.area || recurso.categoria} />}
              <span className="badge bg-ink/10 text-ink">{recurso.tipo}</span>
            </div>
          </div>
        </div>

        <p className="text-ink/60 text-sm leading-relaxed mb-6">{recurso.descripcion}</p>

        {resumen && (
          <div className="flex items-center gap-3 mb-6 p-3 bg-cream rounded-lg">
            <Estrellas puntuacion={resumen.promedio || 0} />
            <span className="text-ink font-bold">{(resumen.promedio || 0).toFixed(1)}</span>
            <span className="text-ink/40 text-xs">({totalValoraciones} valoraciones)</span>
          </div>
        )}

        {(recurso.subidoPorCorreo || recurso.grado) && (
          <div className="grid grid-cols-2 gap-3 mb-6 text-sm">
            {recurso.subidoPorCorreo && (
              <div>
                <span className="text-ink/40 text-xs uppercase tracking-wide">Subido por</span>
                <p className="text-ink font-medium mt-0.5">{recurso.subidoPorCorreo}</p>
              </div>
            )}
            {recurso.grado && (
              <div>
                <span className="text-ink/40 text-xs uppercase tracking-wide">Grado/Nivel</span>
                <p className="text-ink font-medium mt-0.5">{recurso.grado}</p>
              </div>
            )}
          </div>
        )}

        <div className="flex flex-wrap gap-3">
          {recurso.tipo === 'PDF' && (
            <button onClick={handleVerPdf} disabled={cargandoPdf} className="btn-primary">
              {cargandoPdf ? <Spinner size="sm" /> : '👁'} Ver PDF
            </button>
          )}
          <button onClick={handleDescargar} disabled={descargando} className="btn-gold">
            {descargando ? <Spinner size="sm" /> : '⬇️'} Descargar
          </button>
          <button onClick={() => setModalVal(true)} className="btn-ghost">⭐ Valorar</button>
          {esAdmin() && (
            <button onClick={handleEliminar} className="btn-ghost text-red-500 border-red-200 hover:bg-red-50">
              🗑 Eliminar
            </button>
          )}
        </div>
      </div>

      {viendoPdf && pdfUrl && (
        <div className="card mb-6">
          <div className="flex items-center justify-between mb-3">
            <h2 className="font-display text-lg text-ink">Vista previa</h2>
            <button onClick={() => setViendoPdf(false)} className="text-xs text-ink/40 hover:text-ink">× Cerrar</button>
          </div>
          <iframe
            src={pdfUrl}
            className="w-full rounded-lg border border-cream-dark"
            style={{ height: '600px' }}
            title={recurso.nombreArchivo}
          />
        </div>
      )}

      <Modal open={modalVal} onClose={() => setModalVal(false)} title="Valorar recurso">
        <div className="space-y-4">
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-2">Puntuación</label>
            <div className="flex gap-2">
              {[1,2,3,4,5].map(n => (
                <button key={n} onClick={() => setPuntuacion(n)}
                  className={`text-2xl transition-transform hover:scale-110 ${n <= puntuacion ? 'text-gold' : 'text-cream-dark'}`}
                >★</button>
              ))}
            </div>
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1.5">Comentario (opcional)</label>
            <textarea value={comentario} onChange={e => setComentario(e.target.value)}
              rows={3} className="input-field resize-none" placeholder="¿Qué te pareció este recurso?" />
          </div>
          <button onClick={handleValorar} disabled={valorandoLoad} className="btn-primary w-full justify-center">
            {valorandoLoad ? <Spinner size="sm" /> : 'Enviar valoración'}
          </button>
        </div>
      </Modal>
    </div>
  )
}

