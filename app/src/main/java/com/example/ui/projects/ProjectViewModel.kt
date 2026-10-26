package com.example.ui.projects

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.designsystem.components.ExportFormat
import com.example.core.utils.ExportService
import com.example.data.local.entities.ProjectEntity
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ProjectListUiState(
  val projects: List<ProjectEntity> = emptyList(),
  val filteredProjects: List<ProjectEntity> = emptyList(),
  val searchQuery: String = "",
  val totalBudgetGlobalDzd: Double = 0.0,
  val totalCommittedGlobalDzd: Double = 0.0,
  val isLoading: Boolean = false
)

class ProjectViewModel(private val repository: ProjectRepository) : ViewModel() {

  private val _searchQuery = MutableStateFlow("")
  private val _projects = repository.getActiveProjects()

  val uiState: StateFlow<ProjectListUiState> = combine(_projects, _searchQuery) { list, query ->
    val filtered = if (query.isBlank()) {
      list
    } else {
      list.filter {
        it.name.contains(query, ignoreCase = true) ||
            it.code.contains(query, ignoreCase = true) ||
            it.client.contains(query, ignoreCase = true) ||
            it.locationCommune.contains(query, ignoreCase = true)
      }
    }

    ProjectListUiState(
      projects = list,
      filteredProjects = filtered,
      searchQuery = query,
      totalBudgetGlobalDzd = list.sumOf { it.totalBudgetDzd },
      totalCommittedGlobalDzd = list.sumOf { it.committedBudgetDzd }
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = ProjectListUiState(isLoading = true)
  )

  fun onSearchQueryChange(query: String) {
    _searchQuery.value = query
  }

  fun getProject(id: String) = repository.getProjectById(id)

  fun getOrderLinesForProject(projectId: String) = repository.getOrderLinesForProject(projectId)

  fun getPurchaseOrdersForProject(projectId: String) = repository.getPurchaseOrdersForProject(projectId)

  fun getReceptionsForProject(projectId: String) = repository.getReceptionsForProject(projectId)

  fun getInvoicesForProject(projectId: String) = repository.getInvoicesForProject(projectId)

  fun saveProject(project: ProjectEntity, onSaved: () -> Unit) {
    viewModelScope.launch {
      repository.saveProject(project)
      onSaved()
    }
  }

  fun archiveProject(id: String, reason: String, onArchived: () -> Unit) {
    viewModelScope.launch {
      repository.softArchiveProject(id, reason)
      onArchived()
    }
  }

  fun exportProjects(context: Context, format: ExportFormat) {
    val projects = uiState.value.filteredProjects
    val headers = listOf("Code", "Nom du Projet", "Client", "Wilaya", "Commune", "Budget Total (DA)", "Budget Engagé (DA)", "Facturé (DA)", "Statut")
    val csv = ExportService.generateCsv(headers, projects) {
      listOf(it.code, it.name, it.client, it.locationWilaya, it.locationCommune, it.totalBudgetDzd.toString(), it.committedBudgetDzd.toString(), it.invoicedBudgetDzd.toString(), it.status)
    }
    ExportService.shareExport(context, "Projets_Chantiers_AcheteurDZ.csv", csv, "text/csv")
  }
}
