public class DataLoader {
    private static DataLoader instance = null;

    private void DataLoader() {
    }

    public static synchronized DataLoader getInstance() {
        if (instance == null) {
            instance = new DataLoader();
        }

        return instance;
    }

    //启动的时候调用
    public int count() {
        try {
            Thread.sleep(1000 * 2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return 20000;
    }

    //加载数据
    public void load(ILoadCallback callback) {
        for (int i = 0; i < 20000; i++) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            callback.onLoad(new ILoadCallback.Event(20000, i, new Address()));
        }

    }


    public static class Address {
        private String type;
        private String address;
        private String pk;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }
    }

    public static interface ILoadCallback {
        public static class Event {
            private int total;
            private int index;

            private Address data;

            public Event(int total, int index, Address data) {
                this.total = total;
                this.index = index;
                this.data = data;
            }
        }


        void onLoad(Event event);
    }
}
