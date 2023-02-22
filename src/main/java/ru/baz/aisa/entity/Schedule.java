package ru.baz.aisa.entity;

import com.vladmihalcea.hibernate.type.array.IntArrayType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.LocalDate;

@TypeDefs({
        @TypeDef(
                name = "int-array",
                typeClass = IntArrayType.class
        )
})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule")
@Getter
@Setter
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "service_id")
    private Long serviceId;

    @Column(name = "slots", columnDefinition = "int[]")
    @Type(type = "int-array")
    private Integer[] slots;
}
