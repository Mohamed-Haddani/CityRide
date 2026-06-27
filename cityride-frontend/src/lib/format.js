// Helpers de formatage pour l'affichage.

export function formatDateTime(value) {
  if (!value) return ''
  const d = new Date(value)
  return d.toLocaleString('fr-FR', {
    weekday: 'short', day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
  })
}

export function formatDate(value) {
  if (!value) return ''
  return new Date(value).toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' })
}

export function formatPrice(value) {
  if (value == null) return ''
  return `${Number(value).toFixed(2)} DH`
}
