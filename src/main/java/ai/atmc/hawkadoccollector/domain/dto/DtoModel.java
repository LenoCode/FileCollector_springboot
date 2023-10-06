package ai.atmc.hawkadoccollector.domain.dto;


/**
 * Idea of this interface is to help with the validation.
 * Because of this code
 *
 *   if(body.getArgs().length > 0){
 *             Object instance = body.getArgs()[0];
 *
 *             if(instance instanceof DtoModel){ ->  BECAUSE ARRAY IS ARRAY OF OBJECTS AND WE CAN'T KNOW WHICH ONE IS THE DTO, THAT IS WHY WE CHECK IF IT IS INSTANCE OF DtoModel
 *                 objectsValidator.validate(body.getArgs()[0]);
 *             }
 *         }
 */
public interface DtoModel {
}
