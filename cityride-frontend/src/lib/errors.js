// Extrait un message d'erreur lisible depuis une erreur Axios.
export function getErrorMessage(error, fallback = 'Une erreur est survenue') {
  const data = error?.response?.data
  if (data?.message) return data.message
  if (data?.fieldErrors) {
    const first = Object.values(data.fieldErrors)[0]
    if (first) return first
  }
  if (error?.message) return error.message
  return fallback
}
