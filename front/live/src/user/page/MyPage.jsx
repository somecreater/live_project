import { useEffect } from 'react';
import { userStateStore } from '../../common/context/userStateStore';
import User from '../component/User';

function MyPage({props}){

  const {getUserInfo, user, isAuthenticated}= userStateStore();
  useEffect(()=>{
    getUserInfo();
  },[]);

  return (
    <div className="container d-flex justify-content-center align-items-center mt-5">
        <User user={user} isAuthenticated={isAuthenticated}/>
    </div>
  );
}

export default MyPage;