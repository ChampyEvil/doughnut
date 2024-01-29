package com.odde.doughnut.entities;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.persistence.*;

@Entity
@Table(name = "note")
@JsonPropertyOrder({"topic", "topicConstructor", "details", "parentId", "updatedAt"})
public class NoteSimple extends NoteBase {
  public NoteSimple() {}
}
