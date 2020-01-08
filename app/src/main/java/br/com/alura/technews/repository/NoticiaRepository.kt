package br.com.alura.technews.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.alura.technews.asynctask.BaseAsyncTask
import br.com.alura.technews.database.dao.NoticiaDAO
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.retrofit.webclient.NoticiaWebClient
import br.com.alura.technews.repository.Resource

class NoticiaRepository(
    private val dao: NoticiaDAO,
    private val webclient: NoticiaWebClient = NoticiaWebClient()
) {

    private val liveData = MutableLiveData<Resource<List<Noticia>?>>()

    fun buscaTodos(): LiveData<Resource<List<Noticia>?>> {
        val atualizaNoticias: (List<Noticia>) -> Unit = {
            liveData.value = Resource(it)
        }
        buscaInterno(quandoSucesso = atualizaNoticias)
        buscaNaApi(
            quandoSucesso = atualizaNoticias,
            quandoFalha = {
                val resourceAtual = liveData.value
                liveData.value = criarResourseDeFalha<List<Noticia>?>(resourceAtual, it)
            }
        )
        return liveData
    }

    fun salva(noticia: Noticia): LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()
        salvaNaApi(
            noticia,
            quandoSucesso = {
                liveData.value = Resource(dado = null)
            },
            quandoFalha = {
                liveData.value = Resource(dado = null, erro = it)
            }
        )
        return liveData
    }

    fun remove(noticia: Noticia) : LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()
        removeNaApi(
            noticia,
            quandoSucesso = {
                liveData.value = Resource(dado = null)
            },
            quandoFalha = {
                liveData.value = Resource(dado = null, erro = it)
            }
        )
        return liveData
    }

    fun edita(noticia: Noticia): LiveData<Resource<Void?>> {
        val liveData = MutableLiveData<Resource<Void?>>()
        editaNaApi(
            noticia,
            quandoSucesso = {
                liveData.value = Resource(dado = null)
            },
            quandoFalha = {
                liveData.value = Resource(dado = null, erro = it)
            }
        )
        return liveData
    }

    fun buscaPorId(noticiaId: Long): LiveData<Noticia> {
        val liveData = MutableLiveData<Noticia>()
        BaseAsyncTask(
            quandoExecuta = {
                dao.buscaPorId(noticiaId)
            },
            quandoFinaliza = {
                liveData.value = it
            })
            .execute()

        return liveData
    }

    private fun buscaNaApi(
        quandoSucesso: (List<Noticia>) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.buscaTodas(
            quandoSucesso = { noticiasNovas ->
                noticiasNovas?.let {
                    salvaInterno(noticiasNovas, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun buscaInterno(quandoSucesso: (List<Noticia>) -> Unit) {
        BaseAsyncTask(quandoExecuta = {
            dao.buscaTodos()
        }, quandoFinaliza = quandoSucesso)
            .execute()
    }


    private fun salvaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.salva(
            noticia,
            quandoSucesso = {
                it?.let { noticiaSalva ->
                    salvaInterno(noticiaSalva, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

    private fun salvaInterno(
        noticias: List<Noticia>,
        quandoSucesso: (noticiasNovas: List<Noticia>) -> Unit
    ) {
        BaseAsyncTask(
            quandoExecuta = {
                dao.salva(noticias)
                dao.buscaTodos()
            }, quandoFinaliza = quandoSucesso
        ).execute()
    }

    private fun salvaInterno(
        noticia: Noticia,
        quandoSucesso: (noticiaNova: Noticia) -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.salva(noticia)
            dao.buscaPorId(noticia.id)
        }, quandoFinaliza = { noticiaEncontrada ->
            noticiaEncontrada?.let {
                quandoSucesso(it)
            }
        }).execute()

    }

    private fun removeNaApi(
        noticia: Noticia,
        quandoSucesso: () -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.remove(
            noticia.id,
            quandoSucesso = {
                removeInterno(noticia, quandoSucesso)
            },
            quandoFalha = quandoFalha
        )
    }


    private fun removeInterno(
        noticia: Noticia,
        quandoSucesso: () -> Unit
    ) {
        BaseAsyncTask(quandoExecuta = {
            dao.remove(noticia)
        }, quandoFinaliza = {
            quandoSucesso()
        }).execute()
    }

    private fun editaNaApi(
        noticia: Noticia,
        quandoSucesso: (noticiaEditada: Noticia) -> Unit,
        quandoFalha: (erro: String?) -> Unit
    ) {
        webclient.edita(
            noticia.id, noticia,
            quandoSucesso = { noticiaEditada ->
                noticiaEditada?.let {
                    salvaInterno(noticiaEditada, quandoSucesso)
                }
            }, quandoFalha = quandoFalha
        )
    }

}
