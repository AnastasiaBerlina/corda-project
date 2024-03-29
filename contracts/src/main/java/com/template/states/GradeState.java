package com.template.states;

import com.template.contracts.GradeContract;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import net.corda.core.serialization.CordaSerializable;

import java.util.Arrays;
import java.util.List;

// *********
// * State *
// *********
@BelongsToContract(GradeContract.class)
@CordaSerializable
public class GradeState implements ContractState {

    private final Party lecturer;
    private final Party student;
    private final long grade;
    private final String subject;

    @ConstructorForDeserialization
    public GradeState(Party lecturer, Party student, long grade, String subject) {
        this.lecturer = lecturer;
        this.student = student;
        this.grade = grade;
        this.subject = subject;
    }

    public Party getLecturer() {
        return lecturer;
    }

    public Party getStudent() {
        return student;
    }

    public long getGrade() {
        return grade;
    }

    public String getSubject() {
        return subject;
    }

    @Override
    public List<AbstractParty> getParticipants() {
        return Arrays.asList(lecturer, student);
    }

    @Override
    public String toString() {
        return "GradeState{" +
                "lecturer=" + lecturer.getName().getOrganisation() +
                ", student=" + student.getName().getOrganisation() +
                ", grade=" + grade +
                ", subject='" + subject + '\'' +
                '}';
    }
}