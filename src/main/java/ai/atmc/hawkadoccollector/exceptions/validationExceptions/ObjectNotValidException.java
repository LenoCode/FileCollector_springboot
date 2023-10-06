package ai.atmc.hawkadoccollector.exceptions.validationExceptions;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.util.Set;
@RequiredArgsConstructor
@AllArgsConstructor
public class ObjectNotValidException extends Exception {

    private Set<String> errorMessages;


    /**
     * Method that returns json string that contains all error messages
     * @return
     */
    @Override
    public String toString(){
        return errorMessages.toString();
    }

}
