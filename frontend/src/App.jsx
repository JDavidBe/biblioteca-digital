import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext'
import Layout from './components/layout/Layout'
import LoginPage from './pages/LoginPage'
import RegistroPage from './pages/RegistroPage'
import HomePage from './pages/HomePage'
import RecursosPage from './pages/RecursosPage'
import RecursoDetallePage from './pages/RecursoDetallePage'
import MisRecursosPage from './pages/MisRecursosPage'
import MisDescargasPage from './pages/MisDescargasPage'
import MisValoracionesPage from './pages/MisValoracionesPage'
import NotificacionesPage from './pages/NotificacionesPage'
import AdminUsuariosPage from './pages/AdminUsuariosPage'
import AdminReportesPage from './pages/AdminReportesPage'

function PrivateRoute({ children, adminOnly = false }) {
  const { usuario } = useAuth()
  if (!usuario) return <Navigate to="/login" replace />
  if (adminOnly && !['ADMIN', 'BIBLIOTECARIO'].includes(usuario?.rol)) {
    return <Navigate to="/app" replace />
  }
  return children
}

function AppRoutes() {
  const { usuario } = useAuth()
  return (
    <Routes>
      <Route path="/" element={<Navigate to={usuario ? '/app' : '/login'} replace />} />
      <Route path="/login"    element={usuario ? <Navigate to="/app" replace /> : <LoginPage />} />
      <Route path="/registro" element={usuario ? <Navigate to="/app" replace /> : <RegistroPage />} />
      <Route path="/app/*" element={
        <PrivateRoute>
          <Layout />
        </PrivateRoute>
      }>
        <Route index element={<HomePage />} />
        <Route path="recursos"       element={<RecursosPage />} />
        <Route path="recursos/:id"   element={<RecursoDetallePage />} />
        <Route path="mis-recursos"   element={<MisRecursosPage />} />
        <Route path="mis-descargas"  element={<MisDescargasPage />} />
        <Route path="valoraciones"   element={<MisValoracionesPage />} />
        <Route path="notificaciones" element={<NotificacionesPage />} />
        <Route path="admin/usuarios" element={<PrivateRoute adminOnly><AdminUsuariosPage /></PrivateRoute>} />
        <Route path="admin/reportes" element={<PrivateRoute adminOnly><AdminReportesPage /></PrivateRoute>} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  )
}
 
