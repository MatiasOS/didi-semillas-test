package com.atixlabs.semillasmiddleware.security.model;

import com.google.common.collect.Sets;
import java.util.Collection;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@Table(name = "menu")
public class Menu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(nullable = false)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "id_parent")
  private Menu parent;

  @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<Menu> items = Sets.newHashSet();

  private String name;

  private String code;

  private String uri;

  private String type;

  @Column(name = "item_order")
  private Integer order;

  public boolean isMainMenu() {
    return parent == null;
  }

  public Menu() {}

  public Menu(
      Integer id,
      Menu parent,
      Collection<Menu> items,
      String name,
      String code,
      String uri,
      String type,
      Integer order) {
    this.id = id;
    this.parent = parent;
    this.items = items;
    this.name = name;
    this.code = code;
    this.uri = uri;
    this.type = type;
    this.order = order;
  }
}
