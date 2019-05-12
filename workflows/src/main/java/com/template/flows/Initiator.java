package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.template.contracts.GradeContract;
import com.template.states.GradeState;
import net.corda.core.flows.CollectSignaturesFlow;
import net.corda.core.flows.FinalityFlow;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.FlowSession;
import net.corda.core.flows.InitiatingFlow;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// ******************
// * Initiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class Initiator extends FlowLogic<SignedTransaction> {
    private final ProgressTracker progressTracker = new ProgressTracker(
            RECEIVING,
            VERIFYING,
            SIGNING,
            COLLECTING_SIGNATURES,
            RECORDING
    );

    private static final ProgressTracker.Step RECEIVING = new ProgressTracker.Step(
            "Waiting for seller trading info");
    private static final ProgressTracker.Step VERIFYING = new ProgressTracker.Step(
            "Verifying seller assets");
    private static final ProgressTracker.Step SIGNING = new ProgressTracker.Step(
            "Generating and signing transaction proposal");
    private static final ProgressTracker.Step COLLECTING_SIGNATURES = new ProgressTracker.Step(
            "Collecting signatures from other parties");
    private static final ProgressTracker.Step RECORDING = new ProgressTracker.Step(
            "Recording completed transaction");

    private final GradeState gradeState;

    public Initiator(GradeState gradeState) {
        this.gradeState = gradeState;
    }

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        Party notary = getServiceHub().getNetworkParameters().getNotaries().get(0).getIdentity();
        Set<FlowSession> otherFlowSessions = gradeState.getParticipants()
                .stream()
                .map(abstractParty -> initiateFlow((Party) abstractParty))
                .collect(Collectors.toSet());
        otherFlowSessions.add(initiateFlow(notary));
        otherFlowSessions.remove(getOurIdentity());
        List<PublicKey> publicKeys = gradeState.getParticipants()
                .stream()
                .map(AbstractParty::getOwningKey)
                .collect(Collectors.toList());
        publicKeys.add(notary.getOwningKey());
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary)
                .addCommand(new GradeContract.GradeWork(), publicKeys).addOutputState(gradeState);

        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        signedTransaction.getTx().toLedgerTransaction(getServiceHub()).verify();
        SignedTransaction fullySignedTx = subFlow(new CollectSignaturesFlow(signedTransaction, otherFlowSessions, progressTracker));
        return subFlow(new FinalityFlow(fullySignedTx, otherFlowSessions));
    }
}
