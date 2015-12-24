/*
 *  Copyright (C) 2016 Teddy Rodriguez (TROD)
 *    email: cia.123trod@gmail.com
 *    github: TROD-123
 *
 *  For Udacity's Android Developer Nanodegree
 *  P3: SuperDuo
 *
 *  Currently for educational purposes only.
 *
 *  Content provider files generated using Benoit Lubek's (BoD)
 *    Android ContentProvider Generator.
 *    (url: https://github.com/BoD/android-contentprovider-generator)
 */
package com.thirdarm.footballscores.provider.player;

import com.thirdarm.footballscores.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A player who is part of a team
 */
public interface PlayerModel extends BaseModel {

    /**
     * The player's first name. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getFirstname();

    /**
     * The player's last name. (String, Not nullable)
     * Cannot be {@code null}.
     */
    @NonNull
    String getLastname();

    /**
     * The player's position. (String, Not nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getPlayerposition();

    /**
     * The player's jersey number. (Integer, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Integer getJerseynumber();

    /**
     * The player's birth date. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getDateofbirth();

    /**
     * The player's nationality. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getNationality();

    /**
     * The player's contract expiry date. (String, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    String getContractuntildate();

    /**
     * The player's market value in the user's locale currency. (Double, Nullable)
     * Can be {@code null}.
     */
    @Nullable
    Double getMarketvalue();
}
