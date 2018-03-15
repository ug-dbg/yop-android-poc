package org.yop.android.model;

import org.yop.orm.annotations.Column;
import org.yop.orm.annotations.Id;
import org.yop.orm.annotations.JoinTable;
import org.yop.orm.annotations.NaturalId;
import org.yop.orm.annotations.Table;
import org.yop.orm.model.Yopable;

@Table(name = "related_to_sample")
public class RelatedToSample implements Yopable {

    @Id
    @Column(name = "id")
    private Long id;

    @NaturalId
    @Column(name = "rate")
    private double rate;

    @NaturalId
    @Column(name = "label")
    private String label;

    @Column(name = "comment")
    private String comment;

    @JoinTable(
        table = "rel_sample_related",
        sourceColumn = "id_related",
        targetColumn = "id_sample"
    )
    private transient Sample sample;

    public RelatedToSample() {}

    public RelatedToSample(double rate, String label, String comment) {
        this.rate = rate;
        this.label = label;
        this.comment = comment;
    }

    @Override
    public Long getId() {
        return id;
    }

    public double getRate() {
        return rate;
    }

    public String getLabel() {
        return label;
    }

    public String getComment() {
        return comment;
    }

    public Sample getSample() {
        return sample;
    }

    @Override
    public String toString() {
        return "RelatedToSample{" +
            "id=" + id +
            ", rate=" + rate +
            ", label='" + label + '\'' +
            ", comment='" + comment + '\'' +
        '}';
    }
}
