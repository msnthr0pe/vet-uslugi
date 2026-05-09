package com.vetuslugi.di

import android.content.Context
import com.vetuslugi.data.local.SearchHistoryManager
import com.vetuslugi.data.local.UserSession
import com.vetuslugi.data.repository.AuthRepositoryImpl
import com.vetuslugi.data.repository.AnimalRepositoryImpl
import com.vetuslugi.data.repository.ClubRepositoryImpl
import com.vetuslugi.data.repository.NewsRepositoryImpl
import com.vetuslugi.data.repository.PlaceRepositoryImpl
import com.vetuslugi.domain.repository.AnimalRepository
import com.vetuslugi.domain.repository.AuthRepository
import com.vetuslugi.domain.repository.ClubRepository
import com.vetuslugi.domain.repository.NewsRepository
import com.vetuslugi.domain.repository.PlaceRepository
import com.vetuslugi.domain.usecase.auth.GetUserUseCase
import com.vetuslugi.domain.usecase.auth.LoginUseCase
import com.vetuslugi.domain.usecase.auth.RegisterUseCase
import com.vetuslugi.domain.usecase.auth.UpdateUserUseCase
import com.vetuslugi.domain.usecase.news.AddNewsUseCase
import com.vetuslugi.domain.usecase.news.GetNewsUseCase
import com.vetuslugi.domain.usecase.animal.AddAnimalUseCase
import com.vetuslugi.domain.usecase.animal.DeleteAnimalUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByNurseryUseCase
import com.vetuslugi.domain.usecase.animal.GetAnimalsByShelterUseCase
import com.vetuslugi.domain.usecase.animal.UpdateAnimalUseCase
import com.vetuslugi.domain.usecase.place.AddClubUseCase
import com.vetuslugi.domain.usecase.place.AddNurseryUseCase
import com.vetuslugi.domain.usecase.place.AddShelterUseCase
import com.vetuslugi.domain.usecase.place.GetClubsByOwnerUseCase
import com.vetuslugi.domain.usecase.place.GetClubsUseCase
import com.vetuslugi.domain.usecase.place.GetNurseriesByOwnerUseCase
import com.vetuslugi.domain.usecase.place.GetNurseriesUseCase
import com.vetuslugi.domain.usecase.place.GetSheltersByOwnerUseCase
import com.vetuslugi.domain.usecase.place.GetSheltersUseCase
import com.vetuslugi.domain.usecase.place.UpdateClubUseCase
import com.vetuslugi.domain.usecase.place.UpdateNurseryUseCase
import com.vetuslugi.domain.usecase.place.UpdateShelterUseCase
import com.vetuslugi.ktor.ApiClient
import com.vetuslugi.presentation.viewmodel.AddAnimalViewModel
import com.vetuslugi.presentation.viewmodel.AddClubViewModel
import com.vetuslugi.presentation.viewmodel.AddNewsViewModel
import com.vetuslugi.presentation.viewmodel.AddPlaceViewModel
import com.vetuslugi.presentation.viewmodel.AnimalInfoViewModel
import com.vetuslugi.presentation.viewmodel.AnimalsViewModel
import com.vetuslugi.presentation.viewmodel.EditProfileViewModel
import com.vetuslugi.presentation.viewmodel.InfoViewModel
import com.vetuslugi.presentation.viewmodel.LoginViewModel
import com.vetuslugi.presentation.viewmodel.NewsViewModel
import com.vetuslugi.presentation.viewmodel.NurseriesViewModel
import com.vetuslugi.presentation.viewmodel.PlacesViewModel
import com.vetuslugi.presentation.viewmodel.ProfileInfoViewModel
import com.vetuslugi.presentation.viewmodel.ProfileViewModel
import com.vetuslugi.presentation.viewmodel.RegisterViewModel
import com.vetuslugi.presentation.viewmodel.SheltersViewModel

class AppContainer(context: Context) {

    private val api = ApiClient.authApi

    val userSession = UserSession(context)
    val searchHistoryManager = SearchHistoryManager(context)

    private val authRepository: AuthRepository = AuthRepositoryImpl(api)
    private val newsRepository: NewsRepository = NewsRepositoryImpl(api)
    private val placeRepository: PlaceRepository = PlaceRepositoryImpl(api)
    private val clubRepository: ClubRepository = ClubRepositoryImpl(api)
    private val animalRepository: AnimalRepository = AnimalRepositoryImpl(api)

    private val loginUseCase = LoginUseCase(authRepository)
    private val getUserUseCase = GetUserUseCase(authRepository)
    private val registerUseCase = RegisterUseCase(authRepository)
    private val updateUserUseCase = UpdateUserUseCase(authRepository)
    private val getNewsUseCase = GetNewsUseCase(newsRepository)
    private val addNewsUseCase = AddNewsUseCase(newsRepository)
    private val getSheltersUseCase = GetSheltersUseCase(placeRepository)
    private val getNurseriesUseCase = GetNurseriesUseCase(placeRepository)
    private val getSheltersByOwnerUseCase = GetSheltersByOwnerUseCase(placeRepository)
    private val getNurseriesByOwnerUseCase = GetNurseriesByOwnerUseCase(placeRepository)
    private val addShelterUseCase = AddShelterUseCase(placeRepository)
    private val addNurseryUseCase = AddNurseryUseCase(placeRepository)
    private val updateShelterUseCase = UpdateShelterUseCase(placeRepository)
    private val updateNurseryUseCase = UpdateNurseryUseCase(placeRepository)
    private val getClubsUseCase = GetClubsUseCase(clubRepository)
    private val getAnimalsByShelterUseCase = GetAnimalsByShelterUseCase(animalRepository)
    private val getAnimalsByNurseryUseCase = GetAnimalsByNurseryUseCase(animalRepository)
    private val addAnimalUseCase = AddAnimalUseCase(animalRepository)
    private val updateAnimalUseCase = UpdateAnimalUseCase(animalRepository)
    private val deleteAnimalUseCase = DeleteAnimalUseCase(animalRepository)
    private val getClubsByOwnerUseCase = GetClubsByOwnerUseCase(clubRepository)
    private val addClubUseCase = AddClubUseCase(clubRepository)
    private val updateClubUseCase = UpdateClubUseCase(clubRepository)

    val loginViewModelFactory = ViewModelFactory {
        LoginViewModel(loginUseCase, getUserUseCase, userSession)
    }
    val registerViewModelFactory = ViewModelFactory {
        RegisterViewModel(registerUseCase)
    }
    val newsViewModelFactory = ViewModelFactory {
        NewsViewModel(getNewsUseCase, searchHistoryManager)
    }
    val sheltersViewModelFactory = ViewModelFactory {
        SheltersViewModel(getSheltersUseCase)
    }
    val nurseriesViewModelFactory = ViewModelFactory {
        NurseriesViewModel(getNurseriesUseCase)
    }
    val placesViewModelFactory = ViewModelFactory {
        PlacesViewModel(getSheltersUseCase, getNurseriesUseCase, getClubsUseCase)
    }
    val profileViewModelFactory = ViewModelFactory {
        ProfileViewModel(getSheltersByOwnerUseCase, getNurseriesByOwnerUseCase, userSession)
    }
    val profileInfoViewModelFactory = ViewModelFactory {
        ProfileInfoViewModel(userSession)
    }
    val editProfileViewModelFactory = ViewModelFactory {
        EditProfileViewModel(updateUserUseCase, userSession)
    }
    val infoViewModelFactory = ViewModelFactory {
        InfoViewModel(updateShelterUseCase, updateNurseryUseCase, updateClubUseCase, getClubsUseCase)
    }
    val addNewsViewModelFactory = ViewModelFactory {
        AddNewsViewModel(addNewsUseCase)
    }
    val addPlaceViewModelFactory = ViewModelFactory {
        AddPlaceViewModel(addShelterUseCase, addNurseryUseCase, getClubsUseCase, userSession)
    }
    val addClubViewModelFactory = ViewModelFactory {
        AddClubViewModel(addClubUseCase, userSession)
    }
    val animalsViewModelFactory = ViewModelFactory {
        AnimalsViewModel(getAnimalsByShelterUseCase, getAnimalsByNurseryUseCase)
    }
    val animalInfoViewModelFactory = ViewModelFactory {
        AnimalInfoViewModel(updateAnimalUseCase, deleteAnimalUseCase)
    }
    val addAnimalViewModelFactory = ViewModelFactory {
        AddAnimalViewModel(addAnimalUseCase)
    }
}
