import { createContext, useContext, useState } from 'react'
import { authApi, usuariosApi } from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(() => {
    try { return JSON.parse(localStorage.getItem('usuario')) } catch { return null }
  })
  const [token, setToken] = useState(() => localStorage.getItem('token'))

  const login = async (correo, password) => {
    const { data } = await authApi.login(correo, password)
    const { token: tk, id: idFromAuth, ...rest } = data.datos

    localStorage.setItem('token', tk)
    setToken(tk)

    // Construir objeto usuario con id (viene del ms-auth con el fix)
    const user = { ...rest, id: idFromAuth }

    // Intentar enriquecer con datos de ms-usuarios (nombre, institucion, etc.)
    try {
      const { data: uData } = await usuariosApi.obtenerPorCorreo(correo, tk)
      if (uData?.datos) {
        Object.assign(user, uData.datos)
        // Siempre preservar el id de auth por si ms-usuarios devuelve otro
        if (idFromAuth) user.id = idFromAuth
      }
    } catch (_) {}

    localStorage.setItem('usuario', JSON.stringify(user))
    setUsuario(user)
    return user
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('usuario')
    setToken(null)
    setUsuario(null)
  }

  const esAdmin   = () => usuario?.rol === 'ADMIN' || usuario?.rol === 'BIBLIOTECARIO'
  const esDocente = () => usuario?.rol === 'DOCENTE'

  return (
    <AuthContext.Provider value={{ usuario, token, login, logout, esAdmin, esDocente }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
