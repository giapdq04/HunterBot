package giap.hunterbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Bot extends TelegramLongPollingBot {

    private static final Dotenv dotenv = Dotenv.load();


    @Override
    public String getBotUsername() {
        return dotenv.get("BOT_USERNAME");
    }

    @Override
    public String getBotToken() {
        return dotenv.get("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
        var user = msg.getFrom();
        var id = user.getId();

        if (msg.isCommand()) {

            switch (msg.getText()) {
                case "/start":
                    startConversation(id);
                    return;
                case "/time":
                    var date = msg.getDate();
                    String convertedTime = changeTime(date);
                    sendText(id, convertedTime);
                    return;
                default:
                    sendText(id, "I don't understand this command");
                    return;
            }
        }

        sendPhotoByURL(id, "https://images2.thanhnien.vn/528068263637045248/2023/4/23/edit-truc-anh-16822518118551137084698.png");
        sendText(id, "thoiii");

    }

    private void sendPhotoByURL(Long id, String url) {
        SendPhoto sp = SendPhoto.builder()
                .chatId(id.toString())
                .photo(new InputFile(url))
                .caption("This is a photo")
                .build();
        try {
            execute(sp);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

//    private void sendPhotoByInputFile(Long id, String photoPath) {
//        SendPhoto sp = SendPhoto.builder()
//                .chatId(id.toString())
//                .photo(new InputFile(new java.io.File(photoPath)))
//                .caption("Ảnh từ file cục bộ")
//                .build();
//        try {
//            execute(sp);
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//    }


    private String changeTime(int date) {
        Instant instant = Instant.ofEpochSecond(date);
        ZonedDateTime dateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(formatter);
    }

    private void startConversation(Long id) {
        sendText(id, "Hello there! How are you doing?");
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).build();    //Message content
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  //We copy from the user
                .chatId(who.toString())      //And send it back to him
                .messageId(msgId)            //Specifying what message
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
