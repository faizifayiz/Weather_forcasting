# Weather Forecasting App 🌦️

## Overview
This is an Android weather forecasting application. The app fetches real-time weather data using the **OpenWeather API** and displays forecasts in a clean, user-friendly interface.  
It also supports **offline place search** by storing previously searched locations in a local SQLite database.


## Features
- 🔎 **City Search with AutoComplete**  
  - Fetches place suggestions online (from OpenWeather Geo API).  
  - Falls back to **offline suggestions from local DB** when offline.  

- 📍 **Weather Forecast**  
  - Hourly forecast with temperature, icons, and precipitation.  
  - Daily forecast with min/max temperature, conditions, and icons.  

- 💾 **Offline Support**  
  - Saves last fetched data locally.  
  - Shows saved forecasts when offline.  

- 🌐 **API Integration**  
  - Uses Retrofit to connect with [OpenWeather API](https://openweathermap.org/api).  

- 🎨 **UI/UX**  
  - Beautiful gradient background.  
  - Scrollable hourly forecast.  
  - Responsive layout for different screen sizes.  

---

## Tech Stack
- **Language**: Java  
- **IDE**: Android Studio  
- **Libraries**:  
  - Retrofit (API calls)  
  - Glide (image loading)  
  - RecyclerView & CardView (UI)  
  - SQLite (local DB)  


