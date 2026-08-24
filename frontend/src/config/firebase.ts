import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyDfhNXRmHiwT6TWQed_jNmRDwoshcHUy50",
  authDomain: "evalorithm.firebaseapp.com",
  projectId: "evalorithm",
  storageBucket: "evalorithm.firebasestorage.app",
  messagingSenderId: "268249521967",
  appId: "1:268249521967:web:4d819660234e7f1e648bda",
  measurementId: "G-R157GGXK2B"
};

const app = initializeApp(firebaseConfig);
export const auth = getAuth(app);
