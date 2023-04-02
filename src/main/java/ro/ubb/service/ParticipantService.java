package ro.ubb.service;

import ro.ubb.domain.Participant;
import ro.ubb.domain.additional.IDGenerator;
import ro.ubb.domain.validators.Validator;
import ro.ubb.repository.Repository;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Optional;

public class ParticipantService {
    private Repository<Integer, Participant> participantRepository;
    private Validator<Participant> participantValidator;

    public ParticipantService(Repository participantRepository, Validator participantValidator) {
        this.participantRepository = participantRepository;
        this.participantValidator = participantValidator;
    }

    public void addParticipant (Integer idUser, Integer idEvent) throws Exception {

        Participant newParticipant = new Participant(IDGenerator.generateId(participantRepository.findAll()), idUser, idEvent);
        participantValidator.validate(newParticipant);
        participantRepository.save(newParticipant);
    }

    public void updateParticipant (Integer id, Integer idUser, Integer idEvent){

        Participant updatedParticipant = new Participant (id, idUser, idEvent);
        updatedParticipant = participantValidator.checkEmptyBeforeUpdate(updatedParticipant,participantRepository.findOne(id).get());
        participantRepository.update(updatedParticipant);
    }

    public List<Participant> getAllEvents () {
        return (List<Participant>) participantRepository.findAll();
    }

    public void deleteEvent(Integer id){
        try {
            if (participantRepository.findOne(id).isPresent())
                participantRepository.delete(id);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Optional<Participant> findEvent(Integer id) {
        participantValidator.validateId(id,participantRepository.findAll());
        return participantRepository.findOne(id);
    }
}
