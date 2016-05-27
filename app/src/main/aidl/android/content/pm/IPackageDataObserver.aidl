// IPackageDataObserver.aidl
package android.content.pm;

// Declare any non-default types here with import statements



/**
 {@hide}
*/
oneway interface IPackageDataObserver{
    void onRemoveCompleted(in String pkgName, boolean successed);
}
