package com.thirdarm.jokesLib;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

/**
 * Starter Pack jokes obtained from Comedy Central.
 * All credit goes to their original contributors.
 */
public class StarterPack {

    private static ArrayList<Pair<String, String>> jokesPack;

    private static final String joke00 = "Mickey and Minnie have been having problems for some time now. After hearing of Barbie and Ken's breakup, they too decide to call it quits. Donald goes to Mickey to console him and says, \"She's been a problem since day one. I'm glad you finally saw that she's crazy.\" Mickey looks at Donald and replies, \"No, I broke up with her because she's f**king Goofy.\"";
    private static final String joke01 = "Opinions are like assholes. Everyone has one, and everyone elses' stinks.";
    private static final String joke02 = "Two West Virginia hicks get married and spend their honeymoon in a local motel. Right before they consummate the marriage, the women says, \"Be gentle, I'm a virgin.\"\n" +
            "The man is visibly upset and storms off to his family's home. He tells them what happened and his dad says, \"If she isn't good enough for her own family, she sure as hell isn't good enough for you!\"";
    private static final String joke03 = "Husband: Shall we try a new positon tonight?\n" +
            "Wife: Sure. You stand by the ironing board, and I'll sit on the couch while drinking beer and farting.";
    private static final String joke04 = "A husband and wife have four sons. The oldest three are tall with red hair and light skin while the youngest son is short with black hair and dark eyes.\n" +
            "The father was on his deathbed when he turned to his wife and said, \"Honey, before I die, be totally honest with me: Is our youngest son my child?\"\n" +
            "The wife replied, \"I swear on everything that's holy that he is your son.\"\n" +
            "With that, the husband passed away. The wife muttered, \"Thank God he didn't ask about the other three.\"";
    private static final String joke05 = "Bill Gates goes to purgatory.\n" +
            "\n" +
            "St. Peter says, \"Now Bill, you have done some good things, and you have done some bad things. Now I am going to let you decide where you want to go\".\n" +
            "\n" +
            "First, St. Peter shows Bill an image of Hell with beautiful women running on beaches. Then, St Peter shows Bill an image of Heaven with robed angels playing harps on clouds.\n" +
            "\n" +
            "Bill chooses Hell.\n" +
            "\n" +
            "About a week later, St. Peter checks in on Bill in Hell and finds him being whipped by demons.\n" +
            "\n" +
            "Bill says to St. Peter, \"What happened to all the beautiful women and the beaches?\"\n" +
            "\n" +
            "St. Peter replies, \"That was just the screen saver.\"";
    private static final String joke06 = "In a murder trial, the defense attorney was cross-examining the coroner:\n" +
            "\n" +
            "\"Before you signed the death certificate, did you take the pulse, listen to the heart or check for breathing?\"\n" +
            "\n" +
            "\"No.\"\n" +
            "\n" +
            "\"So, when you signed the death certificate, you weren't sure the man was dead, were you?\"\n" +
            "\n" +
            "\"Well, the man's brain was in a jar on my desk, but I suppose he could have still been practicing law for a living.\"";
    private static final String joke07 = "1. Lower corner of screen has the words \"Etch-a-sketch\" on it. \n" +
            "2. It's celebrity spokesman is that \"Hey Vern!\" guy. \n" +
            "3. In order to start it you need some jumper cables and a friend's car. \n" +
            "4. It's slogan is \"Pentium: redefining mathematics\". \n" +
            "5. The \"quick reference\" manual is 120 pages long. \n" +
            "6. Whenever you turn it on, all the dogs in your neighborhood start howling. \n" +
            "7. The screen often displays the message, \"Ain't it break time yet?\" \n" +
            "8. The manual contains only one sentence: \"Good Luck!\" \n" +
            "9. The only chip inside is a Dorito. \n" +
            "10. You've decided that your computer is an excellent addition to your fabulous paperweight collection.";
    private static final String joke08 = "A gynecologist tired of his profession, and wanting less responsibility, decided a career change was in order. After some serious thought, he decided that being an engine mechanic, something he had once enjoyed prior to college, would be a good choice. However, it had been a long time since he had tinkered with an engine and he knew that in order to compete with the younger workforce, he would have to go to school.\n" +
            "He enrolled in a technical institute that specialized in teaching auto mechanics. He aced the course, but the final exam required each student to completely strip and reassemble an engine. It was with some trepidation that he took the test. At completion, he turned the engine over to his instructors for evaluation and awaited his final grade.\n" +
            "When they were handed out, he did a double take at the 150% grade he received. Rather confused, he asked his instructors how it was possible to have a grade like this. \"It is really quite simple,\" they said. \"We gave you 50% for correctly disassembling the engine, 50% for correctly reassembling it, and an additional 50% for doing it all through the muffler.\"";
    private static final String joke09 = "A woman arrives at the Pearly Gates and finds St. Peter is not there, but a computer terminal is sitting next to the arch.\n" +
            "She walks up to it and sees, \"Welcome to www.Heaven.com. Please enter your User ID and Password to continue.\"\n" +
            "She doesn't have either, but underneath the fields is a small line reading:\n" +
            "\"Forgot your ID or Password? Click Here.\" So she does.\n" +
            "Up pops a screen that reads, \"Please enter at least two of the following, and your pasword and ID will be e-mailed to you.\" The fields included \"Name,\" \"Date of birth,\" \"Date of death,\" and \"Favorite Food.\"\n" +
            "The woman enters her name and date of birth, and clicks \"Submit.\"\n" +
            "Up pops another screen that reads, \"We are sorry, we did not find a match in our database. Would you like to register?\" So the woman clicks the button marked \"Yes.\"\n" +
            "A long and detailed form appears on the screen, and the woman spends some time filling it out. Then she clicks the \"Submit\" button.\n" +
            "Now she is faced with a screen reading, \"We are sorry, this service is temporarily unavailable. Please try again later.\"\n" +
            "There is a button marked \"Back.\" She clicks it.\n" +
            "A new page appears.\n" +
            "It reads, \"Welcome to www.Purgatory.com. Please enter your User ID and Password to continue...\"";

    private static final String title00 = "A Disney Break-Up";
    private static final String title01 = "A Piece of Ass";
    private static final String title02 = "A Virgin Hick";
    private static final String title03 = "A Daring New Position";
    private static final String title04 = "A Father's Last Request";
    private static final String title05 = "Bill Gates in Hell";
    private static final String title06 = "Brainless Lawyers";
    private static final String title07 = "10 reasons you know you bought a bad computer";
    private static final String title08 = "Assembly Required";
    private static final String title09 = "E-VIL";

    public StarterPack() {
        jokesPack = new ArrayList<>();
        String[] jokes = { joke00, joke01, joke02, joke03, joke04, joke05, joke06, joke07, joke08, joke09 };
        String[] titles = { title00, title01, title02, title03, title04, title05, title06, title07, title08, title09 };

        // Generate java pairs (using the apache commons lang library) of corresponding title-joke
        for (int i = 0; i < jokes.length; i++) {
            jokesPack.add(Pair.of(titles[i], jokes[i]));
        }
    }

    public ArrayList<Pair<String, String>> getJokesPack() {
        return jokesPack;
    }
}
