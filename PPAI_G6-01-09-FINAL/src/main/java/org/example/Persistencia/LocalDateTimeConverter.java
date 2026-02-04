/**
 * Converter JPA para persistir LocalDateTime como TEXT en SQLite.
 *
 * SQLite no posee un tipo nativo de fecha/hora, por lo que Hibernate
 * tiende a almacenar los valores como timestamps numéricos
 * Este converter transforma automáticamente los LocalDateTime a un
 * formato de texto legible (yyyy-MM-dd HH:mm:ss) al persistir,
 * y los reconstruye al leerlos desde la base de datos.
 *
 * Se aplica de forma global a todos los atributos LocalDateTime
 * gracias a la anotación @Converter(autoApply = true).
 */

package org.example.Persistencia;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, String> {

    // Formato legible y estándar para fechas en SQLite
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Convierte LocalDateTime a String antes de persistir en la base
    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute == null ? null : attribute.format(FMT);
    }

    // Convierte el texto almacenado en la base nuevamente a LocalDateTime
    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return (dbData == null || dbData.isBlank())
                ? null
                : LocalDateTime.parse(dbData, FMT);
    }
}
