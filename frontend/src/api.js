import axios from 'axios'

function crearInstancia(baseURL) {
  const instancia = axios.create({ baseURL })

  instancia.interceptors.request.use(cfg => {
    const token = localStorage.getItem('token')
    if (token) cfg.headers.Authorization = `Bearer ${token}`
    return cfg
  })

  instancia.interceptors.response.use(
    r => r,
    err => {
      if (err.response?.status === 401) {
        localStorage.removeItem('token')
        localStorage.removeItem('usuario')
        window.location.href = '/login'
      }
      return Promise.reject(err)
    }
  )

  return instancia
}

const authAxios           = crearInstancia(import.meta.env.VITE_API_AUTH_URL)
const usuariosAxios       = crearInstancia(import.meta.env.VITE_API_USUARIOS_URL)
const recursosAxios       = crearInstancia(import.meta.env.VITE_API_RECURSOS_URL)
const valoracionesAxios   = crearInstancia(import.meta.env.VITE_API_VALORACIONES_URL)
const descargasAxios      = crearInstancia(import.meta.env.VITE_API_DESCARGAS_URL)
const reportesAxios       = crearInstancia(import.meta.env.VITE_API_REPORTES_URL)
const notificacionesAxios = crearInstancia(import.meta.env.VITE_API_NOTIFICACIONES_URL)

export const authApi = {
  login:              (correo, password)      => authAxios.post('/api/auth/login', { correo, password }),
  registro:           (correo, password, rol) => authAxios.post('/api/auth/registro', { correo, password, rol }),
  validar:            ()                      => authAxios.get('/api/auth/validar'),
  eliminarCredencial: (correo)                => authAxios.delete(`/api/auth/credencial/${encodeURIComponent(correo)}`),
}

export const usuariosApi = {
  listar:           (rol)           => usuariosAxios.get('/api/usuarios', { params: rol ? { rol } : {} }),
  obtener:          (id)            => usuariosAxios.get(`/api/usuarios/${id}`),
  obtenerPorCorreo: (correo, token) => usuariosAxios.get(`/api/usuarios/correo/${correo}`, {
                                         headers: { Authorization: `Bearer ${token}` }
                                       }),
  crear:            (data)          => usuariosAxios.post('/api/usuarios', data),
  editar:           (id, data)      => usuariosAxios.put(`/api/usuarios/${id}`, data),
  eliminar:         (id)            => usuariosAxios.delete(`/api/usuarios/${id}`),
}

export const recursosApi = {
  listar: (params) => {
    const p = {}
    if (params?.q || params?.titulo)       p.q    = params.q || params.titulo
    if (params?.area || params?.categoria) p.area = params.area || params.categoria
    if (params?.grado)                     p.grado = params.grado
    return recursosAxios.get('/api/recursos', { params: Object.keys(p).length ? p : undefined })
  },
  obtener:     (id)             => recursosAxios.get(`/api/recursos/${id}`),
  obtenerBlob: (id)             => recursosAxios.get(`/api/recursos/${id}/descargar`, { responseType: 'blob' }),
  publicar:    (datos, archivo) => {
    const form = new FormData()
    form.append('datos', new Blob([JSON.stringify(datos)], { type: 'application/json' }))
    form.append('archivo', archivo)
    return recursosAxios.post('/api/recursos', form)
  },
  editar:      (id, data)       => recursosAxios.put(`/api/recursos/${id}`, data),
  eliminar:    (id)             => recursosAxios.delete(`/api/recursos/${id}`),
  descargar:   (id, nombre)     =>
    recursosAxios.get(`/api/recursos/${id}/descargar`, { responseType: 'blob' }).then(r => {
      const url = URL.createObjectURL(new Blob([r.data]))
      const a = document.createElement('a')
      a.href = url; a.download = nombre || 'recurso'; a.click()
      URL.revokeObjectURL(url)
    }),
  misRecursos: (uid) => recursosAxios.get(`/api/recursos/usuario/${uid}`),
}

export const valoracionesApi = {
  calificar:  (data) => valoracionesAxios.post('/api/valoraciones', data),
  porRecurso: (rid)  => valoracionesAxios.get(`/api/valoraciones/recurso/${rid}`),
  resumen:    (rid)  => valoracionesAxios.get(`/api/valoraciones/recurso/${rid}/resumen`),
  porUsuario: (uid)  => valoracionesAxios.get(`/api/valoraciones/usuario/${uid}`),
  eliminar:   (id)   => valoracionesAxios.delete(`/api/valoraciones/${id}`),
}

export const descargasApi = {
  registrar:  (data) => descargasAxios.post('/api/descargas', data),
  porUsuario: (uid)  => descargasAxios.get(`/api/descargas/usuario/${uid}`),
  top:        ()     => descargasAxios.get('/api/descargas/top'),
  recientes:  ()     => descargasAxios.get('/api/descargas/recientes'),
}

export const reportesApi = {
  resumen:   ()     => reportesAxios.get('/api/reportes/resumen'),
  recientes: ()     => reportesAxios.get('/api/reportes/actividad/recientes'),
  registrar: (data) => reportesAxios.post('/api/reportes/actividad', data),
}

export const notificacionesApi = {
  porUsuario:   (uid)  => notificacionesAxios.get(`/api/notificaciones/usuario/${uid}`),
  pendientes:   ()     => notificacionesAxios.get('/api/notificaciones/pendientes'),
  bienvenida:   (data) => notificacionesAxios.post('/api/notificaciones/bienvenida', data),
  nuevoRecurso: (data) => notificacionesAxios.post('/api/notificaciones/nuevo-recurso', data),
  descarga:     (data) => notificacionesAxios.post('/api/notificaciones/descarga', data),
}

export default authAxios
