package org.yop.android.model;

import org.yop.orm.annotations.Column;
import org.yop.orm.annotations.Id;
import org.yop.orm.annotations.JoinTable;
import org.yop.orm.annotations.NaturalId;
import org.yop.orm.annotations.Table;
import org.yop.orm.model.Yopable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "sample")
public class Sample implements Yopable {

    @Id
    @Column(name = "id")
    private Long id;

    @NaturalId
    @Column(name = "name")
    private String name;

    @NaturalId
    @Column(name = "timestamp")
    private LocalDateTime timeStamp;

    @JoinTable(
        table = "rel_sample_related",
        sourceColumn = "id_sample",
        targetColumn = "id_related"
    )
    private List<RelatedToSample> related = new ArrayList<>();

    public Sample() {}

    public Sample(String name, LocalDateTime timeStamp) {
        this.name = name;
        this.timeStamp = timeStamp;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public List<RelatedToSample> getRelated() {
        return related;
    }

    @Override
    public String toString() {
        return "Sample{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", timeStamp=" + timeStamp +
        '}';
    }
}
