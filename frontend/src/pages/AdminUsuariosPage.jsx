import { useState, useEffect } from 'react'
import { usuariosApi, authApi, notificacionesApi } from '../api'
import { Spinner, EmptyState, Alert, RolBadge, Modal } from '../components/ui'

const ROLES = ['ADMIN', 'BIBLIOTECARIO', 'DOCENTE', 'ESTUDIANTE']

export default function AdminUsuariosPage() {
  const [usuarios, setUsuarios] = useState([])
  const [loading, setLoading] = useState(true)
  const [filtroRol, setFiltroRol] = useState('')
  const [msg, setMsg] = useState('')
  const [error, setError] = useState('')
  const [editModal, setEditModal] = useState(false)
  const [editUser, setEditUser] = useState(null)
  const [editForm, setEditForm] = useState({})
  const [guardando, setGuardando] = useState(false)
  const [crearModal, setCrearModal] = useState(false)
  const [crearForm, setCrearForm] = useState({ nombre: '', correo: '', password: '', institucion: 'N/A', grado: 'N/A', rol: 'ESTUDIANTE' })
  const [creando, setCreando] = useState(false)

  const cargar = async () => {
    setLoading(true)
    try {
      const { data } = await usuariosApi.listar(filtroRol || undefined)
      setUsuarios(Array.isArray(data?.datos) ? data.datos : [])
    } catch { setUsuarios([]) }
    finally { setLoading(false) }
  }

  useEffect(() => { cargar() }, [filtroRol])

  const abrirEditar = (u) => {
    setEditUser(u)
    setEditForm({ correo: u.correo, rol: u.rol, nombre: u.nombre || '' })
    setEditModal(true)
  }

  const handleGuardar = async () => {
    setGuardando(true)
    setError('')
    try {
      await usuariosApi.editar(editUser.id, editForm)
      setMsg('✓ Usuario actualizado')
      setEditModal(false)
      cargar()
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al actualizar')
    } finally { setGuardando(false) }
  }

  const handleEliminar = async (id, correo) => {
    if (!confirm('¿Eliminar este usuario? Esta acción no se puede deshacer.')) return
    setError('')
    try {
      await usuariosApi.eliminar(id)
      // Eliminar credencial en ms-auth (no bloquea si falla)
      authApi.eliminarCredencial(correo).catch(() => {})
      setMsg('✓ Usuario eliminado')
    } catch {
      setError('Error al eliminar el usuario')
    } finally {
      // Siempre recargar la lista, sin importar si hubo error
      cargar()
    }
  }

  const handleCrear = async () => {
    if (!crearForm.password || crearForm.password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres')
      return
    }
    setCreando(true)
    setError('')
    try {
      // 1. Crear perfil en ms-usuarios
      await usuariosApi.crear(crearForm)
      // 2. Crear credencial en ms-auth para que pueda iniciar sesión
      await authApi.registro(crearForm.correo, crearForm.password, crearForm.rol)
      notificacionesApi.bienvenida({ correo: 'jdavidbernalb@gmail.com', usuarioId: 0, nombre: 'Admin: nuevo usuario creado - ' + crearForm.correo }).catch(() => {})
      setMsg('✓ Usuario creado correctamente')
      setCrearModal(false)
      setCrearForm({ nombre: '', correo: '', password: '', institucion: 'N/A', grado: 'N/A', rol: 'ESTUDIANTE' })
      cargar()
    } catch (err) {
      setError(err.response?.data?.mensaje || 'Error al crear usuario')
    } finally { setCreando(false) }
  }

  return (
    <div className="fade-in">
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="page-title">Usuarios</h1>
          <p className="text-ink/40 text-sm">Gestiona los usuarios del sistema</p>
        </div>
        <div className="flex gap-3">
          <select value={filtroRol} onChange={e => setFiltroRol(e.target.value)} className="input-field w-44">
            <option value="">Todos los roles</option>
            {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
          </select>
          <button onClick={() => setCrearModal(true)} className="btn-primary">
            + Crear usuario
          </button>
        </div>
      </div>

      {error && <div className="mb-4"><Alert type="error">{error}</Alert></div>}
      {msg && <div className="mb-4"><Alert type="success">{msg}</Alert></div>}

      {loading ? (
        <div className="flex justify-center py-20"><Spinner size="lg" /></div>
      ) : usuarios.length === 0 ? (
        <EmptyState icon="👥" title="No hay usuarios" desc="Prueba con otro filtro" />
      ) : (
        <div className="card p-0 overflow-hidden">
          <table className="w-full text-sm">
            <thead className="bg-cream-dark">
              <tr>
                <th className="text-left px-4 py-3 text-xs font-medium text-ink/60 uppercase tracking-wide">ID</th>
                <th className="text-left px-4 py-3 text-xs font-medium text-ink/60 uppercase tracking-wide">Correo</th>
                <th className="text-left px-4 py-3 text-xs font-medium text-ink/60 uppercase tracking-wide">Rol</th>
                <th className="text-right px-4 py-3 text-xs font-medium text-ink/60 uppercase tracking-wide">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-cream-dark">
              {usuarios.map(u => (
                <tr key={u.id} className="hover:bg-cream/50 transition-colors">
                  <td className="px-4 py-3 text-ink/40 font-mono text-xs">{u.id}</td>
                  <td className="px-4 py-3 font-medium text-ink">{u.correo}</td>
                  <td className="px-4 py-3"><RolBadge rol={u.rol} /></td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => abrirEditar(u)} className="text-xs text-gold-dark hover:underline mr-3">Editar</button>
                    <button onClick={() => handleEliminar(u.id, u.correo)} className="text-xs text-red-400 hover:text-red-600">Eliminar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      <Modal open={editModal} onClose={() => setEditModal(false)} title="Editar usuario">
        <div className="space-y-3">
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Correo</label>
            <input value={editForm.correo || ''} onChange={e => setEditForm({...editForm, correo: e.target.value})} className="input-field" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Rol</label>
            <select value={editForm.rol || ''} onChange={e => setEditForm({...editForm, rol: e.target.value})} className="input-field">
              {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
          <button onClick={handleGuardar} disabled={guardando} className="btn-primary w-full justify-center">
            {guardando ? 'Guardando...' : 'Guardar cambios'}
          </button>
        </div>
      </Modal>

      <Modal open={crearModal} onClose={() => setCrearModal(false)} title="Crear usuario">
        <div className="space-y-3">
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Nombre</label>
            <input value={crearForm.nombre} onChange={e => setCrearForm({...crearForm, nombre: e.target.value})} className="input-field" placeholder="Nombre completo" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Correo</label>
            <input value={crearForm.correo} onChange={e => setCrearForm({...crearForm, correo: e.target.value})} className="input-field" placeholder="correo@ejemplo.com" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Contraseña *</label>
            <input type="password" value={crearForm.password} onChange={e => setCrearForm({...crearForm, password: e.target.value})} className="input-field" placeholder="Mínimo 6 caracteres" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Institución</label>
            <input value={crearForm.institucion} onChange={e => setCrearForm({...crearForm, institucion: e.target.value})} className="input-field" placeholder="Institución" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Grado</label>
            <input value={crearForm.grado} onChange={e => setCrearForm({...crearForm, grado: e.target.value})} className="input-field" placeholder="Grado" />
          </div>
          <div>
            <label className="text-xs font-medium text-ink/60 uppercase tracking-wide block mb-1">Rol</label>
            <select value={crearForm.rol} onChange={e => setCrearForm({...crearForm, rol: e.target.value})} className="input-field">
              {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
            </select>
          </div>
          <button onClick={handleCrear} disabled={creando} className="btn-primary w-full justify-center">
            {creando ? 'Creando...' : 'Crear usuario'}
          </button>
        </div>
      </Modal>
    </div>
  )
}




