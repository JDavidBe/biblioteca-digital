import { useEffect } from 'react'

export function Spinner({ size = 'md' }) {
  const s = { sm: 'w-4 h-4', md: 'w-6 h-6', lg: 'w-10 h-10' }[size]
  return (
    <div className={`${s} border-2 border-gold border-t-transparent rounded-full animate-spin`} />
  )
}

export function Alert({ type = 'error', children }) {
  const styles = {
    error: 'bg-red-50 border-red-200 text-red-700',
    success: 'bg-emerald-50 border-emerald-200 text-emerald-700',
    info: 'bg-blue-50 border-blue-200 text-blue-700',
  }
  return (
    <div className={`border rounded-lg px-4 py-3 text-sm ${styles[type]}`}>
      {children}
    </div>
  )
}

export function RolBadge({ rol }) {
  const styles = {
    ADMIN: 'bg-ink text-cream',
    BIBLIOTECARIO: 'bg-ink-muted text-cream',
    DOCENTE: 'bg-gold text-ink',
    ESTUDIANTE: 'bg-cream-dark text-ink',
  }
  return (
    <span className={`badge ${styles[rol] || 'bg-gray-100 text-gray-600'}`}>
      {rol}
    </span>
  )
}

export function CategoriaBadge({ categoria }) {
  return (
    <span className="badge bg-ink/10 text-ink">
      {categoria}
    </span>
  )
}

export function Estrellas({ puntuacion = 0, max = 5, small = false }) {
  return (
    <div className={`flex gap-0.5 ${small ? 'text-sm' : 'text-base'}`}>
      {Array.from({ length: max }).map((_, i) => (
        <span key={i} className={i < Math.round(puntuacion) ? 'text-gold' : 'text-cream-dark'}>★</span>
      ))}
    </div>
  )
}

export function EmptyState({ icon = '📭', title, desc }) {
  return (
    <div className="text-center py-16 text-ink/40">
      <div className="text-5xl mb-3">{icon}</div>
      <p className="font-display text-lg text-ink/60">{title}</p>
      {desc && <p className="text-sm mt-1">{desc}</p>}
    </div>
  )
}

export function Modal({ open, onClose, title, children }) {
  useEffect(() => {
    if (open) document.body.style.overflow = 'hidden'
    else document.body.style.overflow = ''
    return () => { document.body.style.overflow = '' }
  }, [open])

  if (!open) return null
  return (
    <div className="fixed inset-0 z-50 flex items-start justify-center bg-ink/40 backdrop-blur-sm p-4 pt-16" onClick={onClose}>
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 fade-in max-h-[90vh] flex flex-col" onClick={e => e.stopPropagation()}>
        <div className="flex items-center justify-between px-6 pt-6 pb-4 border-b border-cream-dark flex-shrink-0">
          <h3 className="font-display text-xl text-ink">{title}</h3>
          <button onClick={onClose} className="text-ink/40 hover:text-ink text-2xl leading-none">×</button>
        </div>
        <div className="p-6 overflow-y-auto flex-1">{children}</div>
      </div>
    </div>
  )
}

export function StatCard({ icon, label, value, color = 'gold' }) {
  const colors = {
    gold: 'bg-gold/10 text-gold-dark',
    ink: 'bg-ink/10 text-ink',
    green: 'bg-emerald-100 text-emerald-700',
    blue: 'bg-blue-100 text-blue-700',
  }
  return (
    <div className="card flex items-center gap-4">
      <div className={`w-12 h-12 rounded-xl flex items-center justify-center text-xl ${colors[color]}`}>
        {icon}
      </div>
      <div>
        <p className="text-2xl font-display font-bold text-ink">{value ?? '—'}</p>
        <p className="text-xs text-ink/50 mt-0.5">{label}</p>
      </div>
    </div>
  )
}
