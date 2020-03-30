package com.atixlabs.semillasmiddleware.excelparser.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "credentials_data")
public class CredentialData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_credentials")
    //@Column(name = "id_credentials")
    private Credentials credentialsId;

    @ManyToOne
    @JoinColumn(name = "id_categories")
    //@Column(name = "id_categories")
    private Categories categoriesId;

    @ManyToOne
    @JoinColumn(name = "id_questions")
    //@Column(name = "id_questions")
    private Questions questionsId;
}
