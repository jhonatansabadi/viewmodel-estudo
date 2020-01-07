package br.com.alura.technews.repository

import br.com.alura.technews.model.Noticia

class Resource<T>(
    val dado: T?,
    val erro: String? = null
)

inline fun <reified T> criarResourseDeFalha(
    resourceAtual: Resource<T?>?,
    it: String?
): Resource<T?>? {
    if (resourceAtual != null)
        return Resource(
            dado = resourceAtual.dado,
            erro = it
        )
    return Resource(
        dado = null,
        erro = it
    )
}
