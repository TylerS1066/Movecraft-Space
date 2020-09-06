package io.github.eirikh1996.movecraftspace.expansion.dynmap

import io.github.eirikh1996.movecraftspace.expansion.Expansion
import io.github.eirikh1996.movecraftspace.expansion.ExpansionState
import io.github.eirikh1996.movecraftspace.objects.PlanetCollection
import io.github.eirikh1996.movecraftspace.objects.StarCollection
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.dynmap.DynmapCommonAPI
import org.dynmap.markers.CircleMarker
import org.dynmap.markers.Marker
import org.dynmap.markers.MarkerAPI

class DynmapExpansion : Expansion() {
    val orbitMarkerByID = HashMap<String, CircleMarker>()
    val planetMarkerByID = HashMap<String, Marker>()
    val moonMarkerByID = HashMap<String, Marker>()
    val starMarkerByID = HashMap<String, Marker>()
    override fun allowedArea(p: Player, loc: Location): Boolean {
        return true
    }

    override fun enable() {
        val dynmap = Bukkit.getPluginManager().getPlugin("dynmap")
        if (dynmap !is DynmapCommonAPI || !dynmap.isEnabled) {
            logger.severe("Dynmap is required, but was not found or disabled")
            state = ExpansionState.DISABLED
            return
        }

        object : BukkitRunnable() {
            val pMarkers = dynmap.markerAPI.getMarkerSet("planets")
            val planetMarkers = if (pMarkers == null) {
                dynmap.markerAPI.createMarkerSet("planets", "Planets", dynmap.markerAPI.markerIcons, false)
            } else {
                pMarkers
            }
            val oMarkers = dynmap.markerAPI.getMarkerSet("orbits")
            val orbitMarkers = if (oMarkers == null) {
                dynmap.markerAPI.createMarkerSet("orbits", "Orbits", dynmap.markerAPI.markerIcons, false)
            } else {
                oMarkers
            }
            val sMarkers = dynmap.markerAPI.getMarkerSet("stars")
            val starMarkers = if (sMarkers == null) {
                dynmap.markerAPI.createMarkerSet("stars", "Stars", dynmap.markerAPI.markerIcons, false)
            } else {
                sMarkers
            }
            override fun run() {
                for (planet in PlanetCollection) {
                    val star = StarCollection.closestStar(planet.orbitCenter.toLocation(planet.space))!!
                    val orbitMarkerID = star.name + "_" + planet.destination.name + "_orbit_" + planet.space.name
                    val orbitRadius = planet.center.distance(planet.orbitCenter).toDouble()
                    val orbitMarker = if (!orbitMarkerByID.containsKey(orbitMarkerID)) {
                        val tempOrbitMarker = orbitMarkers.findCircleMarker(orbitMarkerID)
                        if (tempOrbitMarker == null) {
                            orbitMarkers.createCircleMarker(orbitMarkerID, orbitMarkerID, false, planet.space.name,
                                planet.orbitCenter.x.toDouble(),
                                planet.orbitCenter.y.toDouble(),
                                planet.orbitCenter.z.toDouble(), orbitRadius, orbitRadius, false)
                        } else {
                            tempOrbitMarker.setCenter(planet.space.name,
                                planet.orbitCenter.x.toDouble(), planet.orbitCenter.y.toDouble(), planet.orbitCenter.z.toDouble()
                            )
                            tempOrbitMarker.setRadius(orbitRadius, orbitRadius)
                            tempOrbitMarker
                        }
                    } else {
                        val tempOrbitMarker = orbitMarkerByID.get(orbitMarkerID)!!
                        tempOrbitMarker.setCenter(planet.space.name,
                            planet.orbitCenter.x.toDouble(), planet.orbitCenter.y.toDouble(), planet.orbitCenter.z.toDouble()
                        )
                        tempOrbitMarker.setRadius(orbitRadius, orbitRadius)
                        tempOrbitMarker
                    }
                    orbitMarker.setFillStyle(0.0, 16711680)
                    val planetMarkerID =  planet.destination.name + "_planet_" + planet.space.name
                    val markerIcon = dynmap.markerAPI.getMarkerIcon("world")
                    val planetMarker = if (!planetMarkerByID.containsKey(planetMarkerID)) {
                        val tempPlanetMarker = planetMarkers.findMarker(planetMarkerID)
                        if (tempPlanetMarker == null) {
                            planetMarkers.createMarker(planetMarkerID, planet.destination.name, planet.space.name,
                                planet.center.x.toDouble(),
                                planet.center.y.toDouble(), planet.center.z.toDouble(), markerIcon, false)
                        } else {
                            tempPlanetMarker.setLocation(planet.space.name,
                                planet.center.x.toDouble(), planet.center.y.toDouble(), planet.center.z.toDouble()
                            )
                            tempPlanetMarker.setMarkerIcon(markerIcon)
                            tempPlanetMarker
                        }
                    } else {
                        val tempPlanetMarker = planetMarkerByID.get(planetMarkerID)!!
                        tempPlanetMarker.setLocation(planet.space.name,
                            planet.center.x.toDouble(), planet.center.y.toDouble(), planet.center.z.toDouble()
                        )
                        tempPlanetMarker.setMarkerIcon(markerIcon)
                        tempPlanetMarker
                    }
                    if (planetMarker != null && !planetMarkerByID.containsKey(planetMarkerID)) {
                        planetMarkerByID.put(planetMarkerID, planetMarker)
                    }
                    if (orbitMarker != null && !orbitMarkerByID.containsKey(orbitMarkerID)) {
                        orbitMarkerByID.put(orbitMarkerID, orbitMarker)
                    }
                }
                for (star in StarCollection) {

                    val starMarkerID =  star.name + "_" + star.space.name
                    val starMarkerIcon = dynmap.markerAPI.getMarkerIcon("sun")
                    val starMarker = if (!starMarkerByID.containsKey(starMarkerID)) {
                        val tempStarMarker = starMarkers.findMarker(starMarkerID)
                        if (tempStarMarker == null) {
                            starMarkers.createMarker(starMarkerID, star.name, star.space.name,
                                star.loc.x.toDouble(), star.loc.y.toDouble(),
                                star.loc.z.toDouble(), starMarkerIcon, false)
                        } else {
                            tempStarMarker.setLocation(star.space.name, star.loc.x.toDouble(),
                                star.loc.y.toDouble(), star.loc.z.toDouble()
                            )
                            tempStarMarker.setMarkerIcon(starMarkerIcon)
                            tempStarMarker
                        }
                    } else {
                        val tempStarMarker = starMarkerByID.get(starMarkerID)!!
                        tempStarMarker.setLocation(star.space.name, star.loc.x.toDouble(), star.loc.y.toDouble(),
                            star.loc.z.toDouble()
                        )
                        tempStarMarker.setMarkerIcon(starMarkerIcon)
                        tempStarMarker
                    }
                    if (starMarker != null && !starMarkerByID.containsKey(starMarkerID)) {
                        starMarkerByID.put(starMarkerID, starMarker)
                    }
                }
                for (planetMarker in planetMarkerByID.keys) {
                    val marker = planetMarkerByID[planetMarker]!!
                    if (PlanetCollection.getPlanetByName(marker.label) != null)
                        continue
                    planetMarkerByID.remove(planetMarker)
                }
                for (orbitMarker in orbitMarkerByID.keys) {
                    val planetName = orbitMarker.split("_")[1]
                    if (PlanetCollection.getPlanetByName(planetName) != null) {
                        continue
                    }
                    orbitMarkerByID.remove(orbitMarker)
                }
                for (starMarker in starMarkerByID.keys) {
                    val marker = starMarkerByID[starMarker]!!
                    if (StarCollection.getStarByName(marker.label) != null)
                        continue
                    starMarkerByID.remove(starMarker)

                }

            }


        }.runTaskTimerAsynchronously(plugin, 0, 200)
    }
}