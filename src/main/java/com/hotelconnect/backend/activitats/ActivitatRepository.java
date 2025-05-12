package com.hotelconnect.backend.activitats;

import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;

@Repository
public interface ActivitatRepository extends JpaRepository<Activitat, Long> {

    Logger logger = LoggerFactory.getLogger(ActivitatRepository.class);

    // Consulta personalizada con la anotación @Query
    @Query("SELECT a FROM Activitat a WHERE a.nom = :nom AND a.lat_activitat = :lat_activitat AND a.lng_activitat = :lng_activitat")
    Optional<Activitat> findActivitatByNomAndLocation(@Param("nom") String nom,
                                                      @Param("lat_activitat") double lat_activitat,
                                                      @Param("lng_activitat") double lng_activitat);

    // Consulta nativa para encontrar actividades dentro de un radio
    @Query(value = """
    SELECT * FROM (
        SELECT a.id, a.nom, a.tipus, a.descripcio, a.lat_activitat, a.lng_activitat,
        (6371 * acos(cos(radians(:lat)) * cos(radians(a.lat_activitat))\s
        * cos(radians(a.lng_activitat) - radians(:lng)) + sin(radians(:lat)) * sin(radians(a.lat_activitat)))) AS distance
        FROM activitats a
    ) AS sub
    WHERE sub.distance <= :radius
    ORDER BY sub.distance
    """, nativeQuery = true)
    List<ActivitatAmbDistancia> findByLocationWithinRadius(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("radius") double radius
    );

    // Metodo para depuración
    default List<ActivitatAmbDistancia> findByLocationWithinRadiusWithLogging(double lat, double lng, double radius) {
        logger.info("Ejecutando consulta con lat: {}, lng: {}, radius: {}", lat, lng, radius);

        // Realizar la consulta original
        List<ActivitatAmbDistancia> result = findByLocationWithinRadius(lat, lng, radius);

        // Imprimir el número de actividades encontradas
        logger.info("Se encontraron {} actividades dentro del radio de {} km", result.size(), radius);

        return result;
    }
}
