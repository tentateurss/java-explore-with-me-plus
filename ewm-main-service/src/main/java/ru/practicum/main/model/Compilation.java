package ru.practicum.main.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "compilations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Compilation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany
    @JoinTable(name = "compilations_events", joinColumns = @JoinColumn(name = "compilation_id"), inverseJoinColumns = @JoinColumn(name = "event_id"))
    private Set<Event> events;

    @Column(nullable = false)
    private Boolean pinned;

    @Column(nullable = false, unique = true)
    private String title;
}
