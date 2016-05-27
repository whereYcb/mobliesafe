package com.ycb.mobliesafe;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.ycb.mobliesafe.bean.BlackNumberInfo;
import com.ycb.mobliesafe.db.dao.BlackNumberDao;

import java.util.List;
import java.util.Random;

/**
 * Created by where on 2016/3/11.
 */
public class TestBlackNumberDao extends AndroidTestCase {
    public Context context;

    @Override
    protected void setUp() throws Exception {
        this.mContext = getContext();
        super.setUp();
    }

    public void testAdd() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        Random random = new Random();
        for (int i = 0; i < 200; i++) {
            long number = 18700000000l + i;
            dao.add(number + "", String.valueOf(random.nextInt(3) + 1));

        }
    }

    public void testDelete() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        boolean delete = dao.delete("18700000000");
        //断言是真
        assertEquals(true, delete);
    }

    public void testFind() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        String number = dao.findNumber("18700000007");
        System.out.println(number);
    }

    public void testFindAll() {
        BlackNumberDao dao = new BlackNumberDao(mContext);
        List<BlackNumberInfo> blackNumberInfos = dao.findAll();
        for (BlackNumberInfo blackNumberInfo :
                blackNumberInfos) {
            System.out.println(blackNumberInfo.getMode() + "" + blackNumberInfo.getNumber());
        }
    }
}
