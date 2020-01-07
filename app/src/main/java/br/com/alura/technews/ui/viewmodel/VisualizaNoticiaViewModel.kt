package br.com.alura.technews.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import br.com.alura.technews.model.Noticia
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.repository.Resource

class VisualizaNoticiaViewModel(
    private val repository: NoticiaRepository
) : ViewModel() {
    fun buscaPorId(noticiaId: Long) : LiveData<Resource<Noticia>> {
        return repository.buscaPorId(noticiaId)
    }
}