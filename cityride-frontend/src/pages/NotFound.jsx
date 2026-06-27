import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="flex min-h-[50vh] flex-col items-center justify-center text-center">
      <p className="text-6xl font-extrabold text-brand-600">404</p>
      <h1 className="mt-4 text-2xl font-bold text-slate-900">Page introuvable</h1>
      <p className="mt-2 text-slate-600">La page que vous cherchez n'existe pas.</p>
      <Link to="/" className="btn-primary mt-6">Retour a l'accueil</Link>
    </div>
  )
}
