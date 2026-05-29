import { NavLink, useNavigate, Outlet } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const navItems = [
  { to: '/app',               label: 'Inicio',           icon: '🏠', exact: true },
  { to: '/app/recursos',      label: 'Recursos',         icon: '📚' },
  { to: '/app/mis-descargas', label: 'Mis Descargas',    icon: '⬇️' },
  { to: '/app/mis-recursos',  label: 'Mis Publicaciones',icon: '📝', roles: ['DOCENTE','ADMIN','BIBLIOTECARIO'] },
  { to: '/app/valoraciones',  label: 'Mis Valoraciones', icon: '⭐' },
  { to: '/app/notificaciones',label: 'Notificaciones',   icon: '🔔' },
]

const adminItems = [
  { to: '/app/admin/usuarios', label: 'Usuarios', icon: '👥' },
  { to: '/app/admin/reportes', label: 'Reportes', icon: '📊' },
]

export default function Layout() {
  const { usuario, logout, esAdmin } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => { logout(); navigate('/login') }

  const visibleItems = navItems.filter(item =>
    !item.roles || item.roles.includes(usuario?.rol)
  )

  return (
    <div className="flex h-screen bg-cream overflow-hidden">
      <aside className="w-64 bg-ink flex flex-col flex-shrink-0">
        <div className="px-6 py-6 border-b border-white/10">
          <h1 className="font-display text-xl text-cream">Biblioteca</h1>
          <p className="text-gold text-xs mt-0.5 tracking-widest uppercase">Digital</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-0.5 overflow-y-auto">
          {visibleItems.map(item => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.exact}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-150 ${
                  isActive
                    ? 'bg-gold text-ink font-semibold'
                    : 'text-cream/70 hover:text-cream hover:bg-white/10'
                }`
              }
            >
              <span className="text-base">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}

          {esAdmin() && (
            <>
              <div className="pt-4 pb-1 px-3">
                <p className="text-white/30 text-xs uppercase tracking-widest">Administración</p>
              </div>
              {adminItems.map(item => (
                <NavLink
                  key={item.to}
                  to={item.to}
                  className={({ isActive }) =>
                    `flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm transition-all duration-150 ${
                      isActive
                        ? 'bg-gold text-ink font-semibold'
                        : 'text-cream/70 hover:text-cream hover:bg-white/10'
                    }`
                  }
                >
                  <span className="text-base">{item.icon}</span>
                  {item.label}
                </NavLink>
              ))}
            </>
          )}
        </nav>

        <div className="px-4 py-4 border-t border-white/10">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-8 h-8 rounded-full bg-gold flex items-center justify-center text-ink font-bold text-sm flex-shrink-0">
              {usuario?.correo?.[0]?.toUpperCase() || '?'}
            </div>
            <div className="min-w-0">
              <p className="text-cream text-xs font-medium truncate">{usuario?.correo}</p>
              <p className="text-gold/70 text-xs">{usuario?.rol}</p>
            </div>
          </div>
          <button
            onClick={handleLogout}
            className="w-full text-left text-xs text-cream/40 hover:text-cream/70 transition-colors px-1"
          >
            Cerrar sesión →
          </button>
        </div>
      </aside>

      <main className="flex-1 overflow-y-auto">
        <div className="p-8 max-w-6xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
