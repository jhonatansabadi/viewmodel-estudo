package br.com.alura.technews.ui.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.alura.technews.repository.NoticiaRepository
import br.com.alura.technews.ui.viewmodel.FormularioViewModel
import br.com.alura.technews.ui.viewmodel.ListaNoticiasViewModel

class FormularioNoticiaViewModelFactory(
    private val repository: NoticiaRepository,
    private val noticiaId: Long
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
        FormularioViewModel(repository, noticiaId) as T
}